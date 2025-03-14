import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { Group } from "../entity/Group";
import { Game } from "../entity/Game";
import { User } from "../entity/User";
import { GroupMember } from "../entity/GroupMember";
import { Not } from "typeorm";
import axios from "axios";
import { ChannelType, Client, GatewayIntentBits, OverwriteResolvable, OverwriteType } from 'discord.js';


const DISCORD_CHANNEL_URL = "https://discord.com/channels/";
const DISCORD_BOT_TOKEN = process.env.DISCORD_BOT_TOKEN || "";
const DISCORD_GUILD_ID = process.env.DISCORD_GUILD_ID || "";

export const getGroups = async (req: Request, res: Response): Promise<void> => {
  try {
    const groupRepository = AppDataSource.getRepository(Group);
    const groups = await groupRepository.find({ relations: ["game", "members"] });
    res.json(groups);
  } catch (error) {
    console.error("Error fetching groups:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const getGroupById = async (req: Request, res: Response): Promise<void> => {
  try {
    const groupRepository = AppDataSource.getRepository(Group);
    const group = await groupRepository.findOne({
      where: { group_id: parseInt(req.params.group_id) },
      relations: ["game", "members"],
    });
    if (group) {
      res.json(group);
    } else {
      res.status(404).json({ message: "Group not found" });
    }
  } catch (error) {
    console.error("Error fetching group by ID:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const createGroup = async (req: Request, res: Response): Promise<void> => {
  try {
    const { game_id, group_name, max_players } = req.body;

    if (!game_id || !group_name || !max_players) {
      res.status(400).json({ message: "All fields are required" });
      return;
    }

    const groupRepository = AppDataSource.getRepository(Group);
    const gameRepository = AppDataSource.getRepository(Game);

    const game = await gameRepository.findOne({
      where: { game_id: parseInt(game_id) },
    });

    if (!game) {
      res.status(404).json({ message: "Game not found" });
      return;
    }

    const group = groupRepository.create({
      game,
      group_name,
      max_players
    });
    await groupRepository.save(group);
    res.status(201).json(group);
  } catch (error) {
    console.error("Error creating group:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const updateGroup = async (req: Request, res: Response): Promise<void> => {
  try {
    const groupRepository = AppDataSource.getRepository(Group);
    const group = await groupRepository.findOne({
      where: { group_id: parseInt(req.params.group_id) },
    });
    if (group) {
      const { group_id, ...updateData } = req.body;
      groupRepository.merge(group, updateData);
      await groupRepository.save(group);
      res.json(group);
    } else {
      res.status(404).json({ message: "Group not found" });
    }
  } catch (error) {
    console.error("Error updating group:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const deleteGroup = async (req: Request, res: Response): Promise<void> => {
  try {
    const groupRepository = AppDataSource.getRepository(Group);
    const group = await groupRepository.findOne({
      where: { group_id: parseInt(req.params.group_id) },
    });
    if (group) {
      await groupRepository.remove(group);
      res.status(204).send();
    } else {
      res.status(404).json({ message: "Group not found" });
    }
  } catch (error) {
    console.error("Error deleting group:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const joinGroup = async (req: Request, res: Response): Promise<void> => {
  try {
    const { group_id } = req.params;
    const { discord_id } = req.body;

    const parsedGroupId = parseInt(group_id);
    if (isNaN(parsedGroupId)) {
      res.status(400).json({ message: "Invalid group ID" });
      return;
    }

    const groupRepository = AppDataSource.getRepository(Group);
    const userRepository = AppDataSource.getRepository(User);
    const groupMemberRepository = AppDataSource.getRepository(GroupMember);

    // Find the group
    const group = await groupRepository.findOne({
      where: { group_id: parsedGroupId },
      relations: ["members"],
    });

    if (!group) {
      res.status(404).json({ message: "Group not found" });
      return;
    }

    // Find the user
    const user = await userRepository.findOne({
      where: { discord_id },
    });

    if (!user) {
      res.status(404).json({ message: "User not found" });
      return;
    }

    // Check if the user is already a member of the group
    const existingMember = await groupMemberRepository.findOne({
      where: {
        group: { group_id: group.group_id },
        user: { discord_id },
      },
    });

    if (existingMember) {
      res.status(409).json({ message: "User is already a member of the group" });
      return;
    }

    // Create a new GroupMember
    const groupMember = groupMemberRepository.create({
      group,
      user,
    });

    await groupMemberRepository.save(groupMember);

    // Reload the group to ensure it has the members relation populated
    const updatedGroup = await groupRepository.findOne({
      where: { group_id: group.group_id },
      relations: ["members"],
    });

    if (updatedGroup) {
      res.json(updatedGroup);
    } else {
      res.status(500).json({ message: "Error reloading group after adding member" });
    }
  } catch (error) {
    res.status(500).json({ message: "Internal server error" });
  }
};

export const leaveGroup = async (req: Request, res: Response): Promise<void> => {
  try {
    const { group_id } = req.params;
    const { discord_id } = req.body;

    const groupRepository = AppDataSource.getRepository(Group);
    const groupMemberRepository = AppDataSource.getRepository(GroupMember);

    // Find the group member
    const groupMember = await groupMemberRepository.findOne({
      where: {
        group: { group_id: parseInt(group_id) },
        user: { discord_id },
      },
    });

    if (!groupMember) {
      res.status(404).json({ message: "Group member not found" });
      return;
    }

    // Remove the group member
    await groupMemberRepository.remove(groupMember);

    // Reload the group to ensure it has the members relation populated
    const updatedGroup = await groupRepository.findOne({
      where: { group_id: parseInt(group_id) },
      relations: ["members"],
    });

    if (updatedGroup) {
      console.log(`Group ${updatedGroup.group_name} has ${updatedGroup.members.length} members after user left`);
      res.json(updatedGroup);
    } else {
      res.status(500).json({ message: "Failed to update group members" });
    }
  } catch (error) {
    console.error("Error leaving group:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const getGroupMembers = async (req: Request, res: Response) => {
  try {
    const groupMemberRepository = AppDataSource.getRepository(GroupMember);

    const groupMembers = await groupMemberRepository.find({
      where: {
        group: { group_id: parseInt(req.params.group_id) },
        user: { discord_id: Not(req.session.user!.discord_id) }
      },
      relations: ["user"]
    });

    if (groupMembers.length) {
      res.json(groupMembers);
    } else {
      res.status(404).json({ message: "No group members found" });
    }
  } catch (error) {
    console.error("Error fetching group members:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const getGroupUrl = async (req: Request, res: Response): Promise<void> => {
  try {
    const groupRepository = AppDataSource.getRepository(Group);
    const group = await groupRepository.findOne({
      where: { group_id: parseInt(req.params.group_id) },
    });
    if (group) {
      res.json({ groupurl: group.groupurl });
    } else {
      res.status(404).json({ message: "Group not found" });
    }
  } catch (error) {
    console.error("Error fetching group URL:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

//TODO: Implement Error Handling
export const createDiscordGroup = async (discord_auth_tokens: string[], discord_ids: string[]): Promise<string> => {
  const client = new Client({
    intents: [GatewayIntentBits.Guilds, GatewayIntentBits.GuildMessages, GatewayIntentBits.GuildMembers, GatewayIntentBits.GuildVoiceStates]
  });

  await client.login(DISCORD_BOT_TOKEN);
  console.log(`Bot login successful`);
  const guild = await client.guilds.fetch(DISCORD_GUILD_ID);
  console.log(`Guild fetched: ${guild}`);

  if (!guild) {
    console.error('Guild not found');
    return "";
  }

  for (let i = 0; i < discord_auth_tokens.length; i++) {
    const discord_id = discord_ids[i];
    const discord_auth_token = discord_auth_tokens[i];
    await addDiscordUserToGuild(discord_auth_token, discord_id, guild.id);
  }
  console.log(`Added users to guild`);

  const randomNumber = Math.floor(Math.random() * 20001);
  const group_name = `Matchmaking-${randomNumber}-Text`;

  const permissionOverwrites: OverwriteResolvable[] = [];

  permissionOverwrites.push({
    id: guild.id,
    deny: ['ViewChannel']
  });

  for (const discord_id of discord_ids) {
    permissionOverwrites.push({
      id: discord_id,
      type: OverwriteType.Member,
      allow: ['ViewChannel', 'SendMessages', 'ReadMessageHistory']
    });
  }

  console.log(`Trying to create channel: ${group_name}`);
  try {
    const channel = await guild.channels.create({
      name: group_name,
      type: ChannelType.GuildText,
      permissionOverwrites: permissionOverwrites
    });
    console.log(`Created channel: ${channel.name}`);

    client.destroy();

    return `${DISCORD_CHANNEL_URL}${guild.id}/${channel.id}`;
  }  catch (err) {
    console.error('Error creating channel:', err);
    return "";
  }
};

const addDiscordUserToGuild = async (discord_auth_token: string, discord_id: string, guild_id: string): Promise<void> => {
  console.log(`Adding Discord user to guild: ${discord_id} ${guild_id} with token ${discord_auth_token}`);
  const response = await axios({
    method: 'put',
    url: `https://discord.com/api/v10/guilds/${guild_id}/members/${discord_id}`,
    data: {
        "access_token": discord_auth_token,
    },
    headers: {
        Authorization: `Bot ${DISCORD_BOT_TOKEN}`,
        'Content-Type': 'application/json',
    },
  });

  if (response.status == 201) {
    console.log("Discord user added to guild successfully.");
  }else if (response.status == 204) {
    console.log("Discord user already in guild.");
  } else {
    console.error("Error adding Discord user to guild " + response.status);
  }
};
