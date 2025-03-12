import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { Game } from "../entity/Game";

export const getGames = async (req: Request, res: Response) => {
  try {
    const gameRepository = AppDataSource.getRepository(Game);
    const games = await gameRepository.find();
    res.json(games);
  } catch (error) {
    console.error("Error fetching games:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const getGameById = async (req: Request, res: Response) => {
  try {
    const gameRepository = AppDataSource.getRepository(Game);
    const game = await gameRepository.findOne({
      where: { game_id: parseInt(req.params.id) },
    });
    if (game) {
      res.json(game);
    } else {
      res.status(404).json({ message: "Game not found" });
    }
  } catch (error) {
    console.error("Error fetching game by ID:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const createGame = async (req: Request, res: Response) => {
  try {
    const gameRepository = AppDataSource.getRepository(Game);
    const existingGame = await gameRepository.findOne({
      where: { game_name: req.body.game_name },
    });
    if (existingGame) {
      res.status(409).json({ message: "Game with this name already exists" });
      return;
    }
    const game = gameRepository.create(req.body);
    await gameRepository.save(game);
    res.status(201).json(game);
  } catch (error) {
    console.error("Error creating game:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const updateGame = async (req: Request, res: Response) => {
  try {
    const gameRepository = AppDataSource.getRepository(Game);
    const game = await gameRepository.findOne({
      where: { game_id: parseInt(req.params.id) },
    });
    if (game) {
      const { _, ...updateData } = req.body;
      gameRepository.merge(game, updateData);
      await gameRepository.save(game);
      res.json(game);
    } else {
      res.status(404).json({ message: "Game not found" });
    }
  } catch (error) {
    console.error("Error updating game:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const deleteGame = async (req: Request, res: Response) => {
  try {
    const gameRepository = AppDataSource.getRepository(Game);
    const game = await gameRepository.findOne({
      where: { game_id: parseInt(req.params.id) },
    });
    if (game) {
      await gameRepository.remove(game);
      res.status(204).send();
    } else {
      res.status(404).json({ message: "Game not found" });
    }
  } catch (error) {
    console.error("Error deleting game:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};