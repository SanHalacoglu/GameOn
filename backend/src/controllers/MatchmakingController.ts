import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { Preferences } from "../entity/Preference";
import { addMatchmakingRequest } from "../services/MatchmakingService";

export const initiateMatchmaking = async (req: Request, res: Response): Promise<void> => {
  const { preference_id } = req.body;
  const discord_access_token = req.session.user?.discord_access_token; // Retrieve discord_access_token from session

  if (!discord_access_token) {
    res.status(401).json({ message: "Unauthorized" });
    return;
  }

  const preferencesRepository = AppDataSource.getRepository(Preferences);
  const preferences = await preferencesRepository.findOne({
    where: { preference_id: parseInt(preference_id) },
    relations: ["user", "game"],
  });

  if (!preferences) {
    res.status(404).json({ message: "Preferences not found" });
    return;
  }

  await addMatchmakingRequest(preferences, discord_access_token); 
  res.status(200).json({ message: "Matchmaking request initiated" });
};