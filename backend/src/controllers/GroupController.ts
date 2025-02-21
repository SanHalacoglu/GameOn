import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { Group } from "../entity/Group";
import { Game } from "../entity/Game";
import { User } from "../entity/User";
import { GroupMember } from "../entity/GroupMember";

export const getGroups = async (req: Request, res: Response): Promise<void> => {
  const groupRepository = AppDataSource.getRepository(Group);
  const groups = await groupRepository.find({ relations: ["game", "members"] });
  res.json(groups);
};

export const getGroupById = async (req: Request, res: Response): Promise<void> => {
  const groupRepository = AppDataSource.getRepository(Group);
  const group = await groupRepository.findOne({
    where: { group_id: parseInt(req.params.id) },
    relations: ["game", "members"],
  });
  if (group) {
    res.json(group);
  } else {
    res.status(404).json({ message: "Group not found" });
  }
};

export const createGroup = async (req: Request, res: Response): Promise<void> => {
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
};

export const updateGroup = async (req: Request, res: Response): Promise<void> => {
  const groupRepository = AppDataSource.getRepository(Group);
  const group = await groupRepository.findOne({
    where: { group_id: parseInt(req.params.id) },
  });
  if (group) {
    groupRepository.merge(group, req.body);
    await groupRepository.save(group);
    res.json(group);
  } else {
    res.status(404).json({ message: "Group not found" });
  }
};

export const deleteGroup = async (req: Request, res: Response): Promise<void> => {
  const groupRepository = AppDataSource.getRepository(Group);
  const group = await groupRepository.findOne({
    where: { group_id: parseInt(req.params.id) },
  });
  if (group) {
    await groupRepository.remove(group);
    res.status(204).send();
  } else {
    res.status(404).json({ message: "Group not found" });
  }
};

export const joinGroup = async (req: Request, res: Response): Promise<void> => {
  const groupRepository = AppDataSource.getRepository(Group);
  const userRepository = AppDataSource.getRepository(User);
  const groupMemberRepository = AppDataSource.getRepository(GroupMember);

  const group = await groupRepository.findOne({
    where: { group_id: parseInt(req.params.id) },
  });

  const user = await userRepository.findOne({
    where: { discord_id: req.body.discord_id },
  });

  if (!group || !user) {
    res.status(404).json({ message: "Group or user not found" });
    return;
  }

  const groupMember = groupMemberRepository.create({
    group,
    user,
  });

  await groupMemberRepository.save(groupMember);
  res.status(201).json(groupMember);
};

export const leaveGroup = async (req: Request, res: Response): Promise<void> => {
  const groupMemberRepository = AppDataSource.getRepository(GroupMember);

  const groupMember = await groupMemberRepository.findOne({
    where: {
      group: { group_id: parseInt(req.params.id) },
      user: { discord_id: req.body.discord_id },
    },
  });

  if (groupMember) {
    await groupMemberRepository.remove(groupMember);
    res.status(204).send();
  } else {
    res.status(404).json({ message: "Group member not found" });
  }
};