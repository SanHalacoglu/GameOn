import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { User } from "../entity/User";
import { GroupMember } from "../entity/GroupMember";

export const getUsers = async (req: Request, res: Response) => {
  try {
    const userRepository = AppDataSource.getRepository(User);
    const users = await userRepository.find({ relations: ["preferences"] });
    res.json(users);
  } catch (error) {
    console.error("Error fetching users:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const getUserById = async (req: Request, res: Response) => {
  try {
    const userRepository = AppDataSource.getRepository(User);
    const user = await userRepository.findOne({
      where: { discord_id: req.params.id },
      relations: ["preferences"],
    });
    if (user) {
      res.json(user);
    } else {
      res.status(404).json({ message: "User not found" });
    }
  } catch (error) {
    console.error("Error fetching user by ID:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const createUser = async (req: Request, res: Response) => {
  const userRepository = AppDataSource.getRepository(User);
  const user = userRepository.create(req.body as User);

  try {
    const existingUser = await userRepository.findOne({
      where: { discord_id: user.discord_id },
      relations: ["preferences"],
    });

    if (existingUser) {
      res.status(409).json({ message: `User with discord_id=${existingUser.discord_id} already exists.` });
    } else {
      await userRepository.save(user);
      res.status(201).json(user);
    }
  } catch (error) {
    console.error("Error creating user:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const updateUser = async (req: Request, res: Response) => {
  try {
    const userRepository = AppDataSource.getRepository(User);
    const user = await userRepository.findOne({
      where: { discord_id: req.params.id },
    });
    if (user) {
      const { discord_id, ...updateData } = req.body; 
      userRepository.merge(user, updateData);
      await userRepository.save(user);
      res.json(user);
    } else {
      res.status(404).json({ message: "User not found" });
    }
  } catch (error) {
    console.error("Error updating user:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const deleteUser = async (req: Request, res: Response) => {
  try {
    const userRepository = AppDataSource.getRepository(User);
    const user = await userRepository.findOne({
      where: { discord_id: req.params.id },
    });
    if (user) {
      await userRepository.remove(user);
      res.status(204).send();
    } else {
      res.status(404).json({ message: "User not found" });
    }
  } catch (error) {
    console.error("Error deleting user:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const getUserGroups = async (req: Request, res: Response) => {
  try {
    const groupMemberRepository = AppDataSource.getRepository(GroupMember);
    const userGroups = await groupMemberRepository.find({
      where: { user: { discord_id: (req.params.id == "session") ? req.session.user!.discord_id : req.params.id} },
      relations: ["group", "group.game"],
    });

    if (userGroups.length > 0) {
      const groups = userGroups.map(groupMember => groupMember.group);
      res.json(groups);
    } else {
      res.status(404).json({ message: "No groups found for this user" });
    }
  } catch (error) {
    console.error("Error fetching user groups:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const banUser = async (req: Request, res: Response) => {
  try {
    const userRepository = AppDataSource.getRepository(User);
    const user = await userRepository.findOne({
      where: { discord_id: req.params.id },
    });
    if (user) {
      user.banned = true;
      await userRepository.save(user);
      res.json(user);
    } else {
      res.status(404).send({ message: "User not found" });
    }
  } catch (error) {
    console.error("Error banning user:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};