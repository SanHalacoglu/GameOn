import { redisClient } from "../redis_client";
import { AppDataSource } from "../data-source";
import { User } from "../entity/User";
import { Game } from "../entity/Game";
import { Group } from "../entity/Group";
import { GroupMember } from "../entity/GroupMember";
import { Preferences } from "../entity/Preference";
import { createDiscordGroup } from "../controllers/GroupController";

const REDISKEYSPACE_MATCHMAKING_STATUS = "matchmaking_status";
const REDISKEYSPACE_MATCHMAKING_BUCKET = "matchmaking_bucket";
const MATCHMAKING_TIMEOUT = 1 * 60 * 1000;
const POLING_INTERVAL = 0.5 * 60 * 1000;

let gameInfoCache: Map<number, number> = new Map();

interface MatchmakingRequest {
  discord_id: string;
  discord_access_token: string;
  skill_level: "casual" | "competitive";
  game_id: number;
  spoken_language: string; //TODO: What if people speak multiple languages? Add people to different buckets keeping track of their tickets.
  timestamp: number;
}

export async function initializeMatchmakingService() {
  try {
    const gameRepository = AppDataSource.getRepository(Game);
    const games = await gameRepository.find();
  
    gameInfoCache.clear();
    games.forEach((game) => {
      gameInfoCache.set(game.game_id, game.group_size);
    });

    setInterval(async () => {
      console.log('Processing matchmaking queue...');
      await processMatchmakingQueue();
    }, 5000);

    setInterval(async () => {
      console.log('Processing timed out matchmaking requests...');
      await processTimedOutMatchmakingRequests();
    }, 5000);

  } catch (error) {
    console.error("Error initializing matchmaking service:", error);
  }
}

/**
 * Adds a matchmaking request to the redis bucket, and sets the user's status to 'in_progress'.
 */
export async function addMatchmakingRequest(preferences: Preferences, discord_access_token: string) {
  const request: MatchmakingRequest = {
    discord_id: preferences.user.discord_id,
    discord_access_token,
    skill_level: preferences.skill_level,
    game_id: preferences.game.game_id,
    spoken_language: preferences.spoken_language,
    timestamp: Date.now(),
  };

  const request_obj = {
    skill_level: preferences.skill_level,
    game_id: preferences.game.game_id,
    spoken_language: preferences.spoken_language,
  }
  const receiptKey = REDISKEYSPACE_MATCHMAKING_STATUS + `:${preferences.user.discord_id}`;
  await redisClient.set(receiptKey, 'in_progress');

  const matchmakingRequestKey = REDISKEYSPACE_MATCHMAKING_BUCKET + `:${JSON.stringify(request_obj)}`;
  await redisClient.rPush(matchmakingRequestKey, JSON.stringify(request));

  console.log(`Added matchmaking request for user ${preferences.user.discord_id}`);
}

export async function getUserMatchmakingStatus(discord_id: string): Promise<string> {
  const status = await redisClient.get(REDISKEYSPACE_MATCHMAKING_STATUS + `:${discord_id}`);
  if (status) {
    return status
  }

  return "not_found";
}

export async function processTimedOutMatchmakingRequests() {
  const matchmakingRequestKeys = await redisClient.keys(REDISKEYSPACE_MATCHMAKING_BUCKET + `:*`);

  for (const key of matchmakingRequestKeys) {
    const currentTime = Date.now();
    const matchmakingRequests = await redisClient.lRange(key, 0, -1);

    if (matchmakingRequests.length > 0) {
      const parsedRequests: MatchmakingRequest[] = matchmakingRequests.map(req => JSON.parse(req));
      let removeCount = 0;

      for (const request of parsedRequests) {
        if (currentTime - request.timestamp > MATCHMAKING_TIMEOUT) {
          console.log(`Matchmaking request for user ${request.discord_id} timed out`);
          await updateMatchmakingStatus(request.discord_id, 'timed_out', POLING_INTERVAL);
          removeCount++;
        } else {
          // Stop checking as soon as we find a non-expired request. Assumption is that requests are ordered by timestamp.
          break;
        }
      }

      if (removeCount > 0) {
        // Remove all timed-out items from the left
        await redisClient.lTrim(key, removeCount, -1);
      }
    }
  }
}

/**
 * Goes through all the matchmaking buckets, checking game part size for each bucket.
 * Pops members from the bucket in parts of the game size, and creates a group with them.
 * Continues popping until there are not enough members left in the bucket to form a group.
 * Creates a matchmaking group with the popped members.
 **/
export async function processMatchmakingQueue() {
  const matchmakingRequestKeys = await redisClient.keys(REDISKEYSPACE_MATCHMAKING_BUCKET + `:*`);

  for (const key of matchmakingRequestKeys) {
    const gameSize = getGameSizeForRedisBucketKey(key);
    if (!gameSize) {
      console.error("Invalid game size for bucket key", key);
      continue;
    }

    while (await redisClient.lLen(key) >= gameSize) {
      const groupMembers = await redisClient.lPopCount(key, gameSize);
      if (groupMembers) {
        const parsedGroupMembers: MatchmakingRequest[] = groupMembers.map((req) => JSON.parse(req));
        await createMatchmakingGroup(parsedGroupMembers);
      }
    }
  }
}

/**
 * Creates a matchmaking group with the given members.
 * Updates status of the matchmaking receipt for each member to 'group_found'.
 */
async function createMatchmakingGroup(members: MatchmakingRequest[]) {
  const userRepository = AppDataSource.getRepository(User);
  const gameRepository = AppDataSource.getRepository(Game);
  const groupRepository = AppDataSource.getRepository(Group);
  const groupMemberRepository = AppDataSource.getRepository(GroupMember);

  const uniqueMembers = members.filter((member, index, self) =>
    index === self.findIndex((m) => m.discord_id === member.discord_id)
  );

  console.log(`Creating group for ${uniqueMembers.length} members. Game ID: ${uniqueMembers[0].game_id}`);
  const users = await Promise.all(uniqueMembers.map((req) => userRepository.findOne({ where: { discord_id: req.discord_id } })));
  const game = await gameRepository.findOne({ where: { game_id: uniqueMembers[0].game_id } });
  console.log("users", users);
  console.log("game", game);
  if (users.includes(null) || !game) {
    console.error("Invalid users or game in matchmaking group");
    return;
  }

  //TODO: Change group name to something more fun?
  const group = groupRepository.create({
    game,
    group_name: `${game.game_name} Matchmaking Group`,
    max_players: game.group_size,
  });

  const discordAuthTokens = members.map(member => member.discord_access_token);
  const discordIds = members.map(member => member.discord_id);

  const groupUrl = await createDiscordGroup(discordAuthTokens, discordIds);
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
      await updateMatchmakingStatus(user.discord_id, 'group_found', POLING_INTERVAL);
    }
  }

  // TODO: Delete Sanity check for group
  const updatedGroup = await groupRepository.findOne({
    where: { group_id: group.group_id },
    relations: ["members"],
  });

  if (updatedGroup) {
    console.log(`Group ${updatedGroup.group_name} has ${updatedGroup.members.length} members`);
  }
}

function getGameSizeForRedisBucketKey(redisBucketKey: string): number | undefined {
  const keyObj = JSON.parse(redisBucketKey.replace(REDISKEYSPACE_MATCHMAKING_BUCKET + `:`, ''));
  console.log("Bucket key obj", keyObj);
  const gameSize = gameInfoCache.get(keyObj.game_id);
  return gameSize
}

async function updateMatchmakingStatus(discord_id: string, status: string, expiryInMilliseconds?: number) {
  const key = REDISKEYSPACE_MATCHMAKING_STATUS + `:${discord_id}`;
  if (expiryInMilliseconds !== undefined) {
    await redisClient.set(key, status, { PX: expiryInMilliseconds });
  } else {
    await redisClient.set(key, status);
  }
}

export async function removeMatchmakingStatus(discord_id: string) {
  const key = REDISKEYSPACE_MATCHMAKING_STATUS + `:${discord_id}`;
  await redisClient.del(key);
}
