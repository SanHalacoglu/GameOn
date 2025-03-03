import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { Preferences } from "../entity/Preference";
import { User } from "../entity/User";
import { Game } from "../entity/Game";

export const getPreferences = async (req: Request, res: Response): Promise<void> => {
  const preferencesRepository = AppDataSource.getRepository(Preferences);
  const preferences = await preferencesRepository.find({ relations: ["user", "game"] });
  res.json(preferences);
};

export const getPreferencesById = async (req: Request, res: Response): Promise<void> => {
  const preferencesRepository = AppDataSource.getRepository(Preferences);
  const preferences = await preferencesRepository.findOne({
    where: { preference_id: parseInt(req.params.id) },
    relations: ["user", "game"],
  });
  if (preferences) {
    res.json(preferences);
  } else {
    res.status(404).json({ message: "Preferences not found" });
  }
};

export const getPreferencesByUserId = async (req: Request, res: Response): Promise<void> => {
  const preferencesRepository = AppDataSource.getRepository(Preferences);
  const userRepository = AppDataSource.getRepository(User);

  const user = await userRepository.findOne({
    where: { discord_id: req.params.userId },
  });

  if (!user) {
    res.status(404).json({ message: "User not found" });
    return;
  }

  const preferences = await preferencesRepository.findOne({
    where: { user: { discord_id: req.params.userId } },
    relations: ["user", "game"],
  });

  if (preferences) {
    res.json(preferences);
  } else {
    res.status(404).json({ message: "Preferences not found" });
  }
};

export const createPreferences = async (req: Request, res: Response): Promise<void> => {
  const { discord_id, spoken_language, time_zone, skill_level, game_id } = req.body;

  if (!discord_id || !spoken_language || !time_zone || !skill_level || !game_id) {
    res.status(400).json({ message: "All fields are required" });
    return;
  }

  const userRepository = AppDataSource.getRepository(User);
  const gameRepository = AppDataSource.getRepository(Game);
  const preferencesRepository = AppDataSource.getRepository(Preferences);

  try {
    const user = await userRepository.findOne({
      where: { discord_id: discord_id },
    });

    if (!user) {
      res.status(404).json({ message: "User not found" });
      return;
    }

    const game = await gameRepository.findOne({
      where: { game_id: parseInt(game_id) },
    });

    if (!game) {
      res.status(404).json({ message: "Game not found" });
      return;
    }

    const preferences = preferencesRepository.create({
      spoken_language,
      time_zone,
      skill_level,
      user,
      game,
    });

    await preferencesRepository.save(preferences);
    res.status(201).json(preferences);
  } catch (error) {
    console.error("Error creating preferences:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const updatePreferences = async (req: Request, res: Response): Promise<void> => {
  const preferencesRepository = AppDataSource.getRepository(Preferences);
  const gameRepository = AppDataSource.getRepository(Game);

  const preferences = await preferencesRepository.findOne({
    where: { preference_id: parseInt(req.params.id) },
    relations: ["game"],
  });

  if (preferences) {
    const { preference_id, game_id, ...updateData } = req.body;

    if (game_id) {
      const game = await gameRepository.findOne({
        where: { game_id: parseInt(game_id) },
      });

      if (!game) {
        res.status(404).json({ message: "Game not found" });
        return;
      }

      preferences.game = game;
    }

    preferencesRepository.merge(preferences, updateData);
    await preferencesRepository.save(preferences);
    res.json(preferences);
  } else {
    res.status(404).json({ message: "Preferences not found" });
  }
};

export const deletePreferences = async (req: Request, res: Response): Promise<void> => {
  const preferencesRepository = AppDataSource.getRepository(Preferences);
  const preferences = await preferencesRepository.findOne({
    where: { preference_id: parseInt(req.params.id) },
  });
  if (preferences) {
    await preferencesRepository.remove(preferences);
    res.status(204).send();
  } else {
    res.status(404).json({ message: "Preferences not found" });
  }
};