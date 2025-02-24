import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { User } from "../entity/User";

export const getUsers = async (req: Request, res: Response) => {
  const userRepository = AppDataSource.getRepository(User);
  const users = await userRepository.find({ relations: ["preferences"] });
  res.json(users);
};

export const getUserById = async (req: Request, res: Response) => {
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
};

export const createUser = async (req: Request, res: Response) => {
  const userRepository = AppDataSource.getRepository(User);
  const user = userRepository.create(req.body as User);

  const existingUser = await userRepository.findOne({
    where: { discord_id: user.discord_id },
    relations: ["preferences"],
  });

  if (existingUser)
    res.status(409).json({ message: `User with discord_id=${existingUser.discord_id} already exists.` })
  else {
    await userRepository.save(user);
    res.status(201).json(user);
  }
};

export const updateUser = async (req: Request, res: Response) => {
  const userRepository = AppDataSource.getRepository(User);
  const user = await userRepository.findOne({
    where: { discord_id: req.params.id },
  });
  if (user) {
    userRepository.merge(user, req.body);
    await userRepository.save(user);
    res.json(user);
  } else {
    res.status(404).json({ message: "User not found" });
  }
};

export const deleteUser = async (req: Request, res: Response) => {
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
};