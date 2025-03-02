import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { Preferences } from "../entity/Preference";
import { addMatchmakingRequest, isUserInMatchmakingQueue, getUserMatchmakingStatus } from "../services/MatchmakingService";

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

  // Check if the user is already in the matchmaking queue
  const { status } = await isUserInMatchmakingQueue(preferences.user.discord_id);
  if (status === "in_progress") {
    res.status(400).json({ message: "User is already in the matchmaking queue" });
    return;
  }

  await addMatchmakingRequest(preferences, discord_access_token); 
  res.status(200).json({ message: "Matchmaking request initiated" });
};

export const checkMatchmakingStatus = async (req: Request, res: Response): Promise<void> => {
  const { discord_id } = req.params;

  const { status, timestamp } = await getUserMatchmakingStatus(discord_id);

  if (status === "in_progress") {
    res.status(200).json({ message: "Matchmaking in progress", timestamp });
  } else if (status === "timed_out") {
    res.status(200).json({ message: "Matchmaking timed out", timestamp });
  } else if (status === "group_found") {
    res.status(200).json({ message: "Group found", timestamp });
  } else {
    res.status(404).json({ message: "Matchmaking not in progress" });
  }
};