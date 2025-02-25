import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { Preferences } from "../entity/Preference";
import { addMatchmakingRequest } from "../services/MatchmakingService";

export const initiateMatchmaking = async (req: Request, res: Response): Promise<void> => {
  const preferencesRepository = AppDataSource.getRepository(Preferences);
  const preferences = await preferencesRepository.findOne({
    where: { preference_id: parseInt(req.body.preference_id) },
    relations: ["user", "game"],
  });

  if (!preferences) {
    res.status(404).json({ message: "Preferences not found" });
    return;
  }

  await addMatchmakingRequest(preferences);
  res.status(200).json({ message: "Matchmaking request initiated" });
};