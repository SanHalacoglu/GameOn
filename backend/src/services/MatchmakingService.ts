import { redisClient } from "../redis_client";
import { AppDataSource } from "../data-source";
import { User } from "../entity/User";
import { Game } from "../entity/Game";
import { Group } from "../entity/Group";
import { GroupMember } from "../entity/GroupMember";
import { Preferences } from "../entity/Preference";
import { createDiscordGroup } from "../controllers/GroupController";

const MATCHMAKING_QUEUE = "matchmaking_queue";
const MATCHMAKING_TIMEOUT = 3 * 60 * 1000; // 3 minutes
const GROUP_SIZE = 3;

interface MatchmakingRequest {
  discord_id: string;
  discord_access_token: string;
  skill_level: "casual" | "competitive";
  game_id: number;
  timestamp: number;
}

/**
 * Adds a matchmaking request to the Redis sorted set.
 * @param preferences - The user's preferences for matchmaking.
 */
export async function addMatchmakingRequest(preferences: Preferences, discord_access_token: string) {
  const request: MatchmakingRequest = {
    discord_id: preferences.user.discord_id,
    discord_access_token,
    skill_level: preferences.skill_level,
    game_id: preferences.game.game_id,
    timestamp: Date.now(),
  };

  const requestKey = `${request.discord_id}-${request.timestamp}`;
  await redisClient.zAdd(MATCHMAKING_QUEUE, {
    score: request.timestamp,
    value: JSON.stringify(request),
  });

  console.log(`Added matchmaking request for user ${preferences.user.discord_id}`);
}

export async function getUserMatchmakingStatus(discord_id: string): Promise<{ status: string, timestamp?: number }> {
  const status = await redisClient.get(`matchmaking_status:${discord_id}`);
  if (status) {
    const [statusValue, timestamp] = status.split(":");
    return { status: statusValue, timestamp: parseInt(timestamp) };
  }
  return isUserInMatchmakingQueue(discord_id);
}

export async function isUserInMatchmakingQueue(discord_id: string): Promise<{ status: string, timestamp?: number }> {
  const now = Date.now();
  const validRequests = await redisClient.zRangeByScore(
    MATCHMAKING_QUEUE,
    now - MATCHMAKING_TIMEOUT,
    now
  );

  const parsedRequests: MatchmakingRequest[] = validRequests.map((req) => JSON.parse(req));
  const userRequest = parsedRequests.find((req) => req.discord_id === discord_id);

  if (userRequest) {
    return { status: "in_progress", timestamp: userRequest.timestamp };
  }

  // Check if the request has timed out
  const allRequests = await redisClient.zRangeByScore(
    MATCHMAKING_QUEUE,
    0,
    now - MATCHMAKING_TIMEOUT
  );

  const timedOutRequests: MatchmakingRequest[] = allRequests.map((req) => JSON.parse(req));
  const timedOutRequest = timedOutRequests.find((req) => req.discord_id === discord_id);

  if (timedOutRequest) {
    return { status: "timed_out", timestamp: timedOutRequest.timestamp };
  }

  return { status: "not_in_progress" };
}


/**
 * Processes the matchmaking queue, forms groups, and removes processed requests.
 */
export async function processMatchmakingQueue() {
  const now = Date.now();

  // Fetch all valid (non-expired) requests
  const validRequests = await redisClient.zRangeByScore(
    MATCHMAKING_QUEUE,
    now - MATCHMAKING_TIMEOUT,
    now
  );

  const parsedRequests: MatchmakingRequest[] = validRequests.map((req) => JSON.parse(req));

  // Group requests by game_id and skill_level
  const groupedRequests: { [key: string]: MatchmakingRequest[] } = {};

  parsedRequests.forEach((req) => {
    const key = `${req.game_id}-${req.skill_level}`;
    if (!groupedRequests[key]) {
      groupedRequests[key] = [];
    }
    groupedRequests[key].push(req);
  });

  // Track processed request keys (discord_id + timestamp)
  const processedRequestKeys: string[] = [];

  // Process each group of requests
  for (const key in groupedRequests) {
    const group = groupedRequests[key];
    if (group.length >= GROUP_SIZE) {
      const members = group.slice(0, GROUP_SIZE);
      await createMatchmakingGroup(members);

      // Track processed requests
      members.forEach((req) => {
        processedRequestKeys.push(`${req.discord_id}-${req.timestamp}`);
        redisClient.set(`matchmaking_status:${req.discord_id}`, `group_found:${req.timestamp}`, 'EX', STATUS_CACHE_EXPIRATION);
      });

      console.log(`Created matchmaking group for game ${group[0].game_id} with skill level ${group[0].skill_level}`);
    }
  }

  // Remove processed requests from the queue
  if (processedRequestKeys.length > 0) {
    const pipeline = redisClient.multi();
    processedRequestKeys.forEach((key) => {
      const [discord_id, timestamp] = key.split("-");
      pipeline.zRemRangeByScore(MATCHMAKING_QUEUE, parseInt(timestamp), parseInt(timestamp));
    });
    await pipeline.exec();
  }

  // Remove expired requests
  const expiredRequests = await redisClient.zRangeByScore(MATCHMAKING_QUEUE, 0, now - MATCHMAKING_TIMEOUT);
  expiredRequests.forEach(async (req) => {
    const { discord_id, timestamp } = JSON.parse(req);
    redisClient.set(`matchmaking_status:${discord_id}`, `timed_out:${timestamp}`, 'EX', STATUS_CACHE_EXPIRATION);
  });
  await redisClient.zRemRangeByScore(MATCHMAKING_QUEUE, 0, now - MATCHMAKING_TIMEOUT);

  console.log(`Processed matchmaking queue. Valid requests: ${validRequests.length}`);
}

async function createMatchmakingGroup(members: MatchmakingRequest[]) {
  const userRepository = AppDataSource.getRepository(User);
  const gameRepository = AppDataSource.getRepository(Game);
  const groupRepository = AppDataSource.getRepository(Group);
  const groupMemberRepository = AppDataSource.getRepository(GroupMember);

  const users = await Promise.all(members.map((req) => userRepository.findOne({ where: { discord_id: req.discord_id } })));
  const game = await gameRepository.findOne({ where: { game_id: members[0].game_id } });

  if (users.includes(null) || !game) {
    console.error("Invalid users or game in matchmaking group");
    return;
  }

  const group = groupRepository.create({
    game,
    group_name: `${game.game_name} Matchmaking Group`,
    max_players: GROUP_SIZE,
  });

  const discordAuthTokens = members.map(member => member.discord_access_token);
  const groupUrl = await createDiscordGroup(discordAuthTokens);
  group.groupurl = groupUrl;

  await groupRepository.save(group);

  for (const user of users) {
    if (user) {
      const groupMember = groupMemberRepository.create({
        group,
        user,
      });
      await groupMemberRepository.save(groupMember);
      console.log(`Added user ${user.discord_id} to group ${group.group_name}`);
    }
  }

  // Reload the group to ensure it has the members relation populated
  const updatedGroup = await groupRepository.findOne({
    where: { group_id: group.group_id },
    relations: ["members"],
  });

  if (updatedGroup) {
    console.log(`Group ${updatedGroup.group_name} has ${updatedGroup.members.length} members`);
  }
}