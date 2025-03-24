import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { Preferences } from "../entity/Preference";
import { addMatchmakingRequest, getUserMatchmakingStatus, removeMatchmakingStatus} from "../services/MatchmakingService";

export const initiateMatchmaking = async (req: Request, res: Response): Promise<void> => {
  const { preference_id } = req.body;
  const discord_access_token = req.session.user?.discord_access_token;

  if (!discord_access_token) {
    res.status(401).json({ message: "Unauthorized" });
    return;
  }

  try {
    const preferencesRepository = AppDataSource.getRepository(Preferences);
    const preferences = await preferencesRepository.findOne({
      where: { preference_id: parseInt(preference_id as string) },
      relations: ["user", "game"],
    }) as Preferences|undefined;

    if (!preferences) {
      res.status(404).json({ message: "Preferences not found" });
      return;
    }

    const receiptExists: boolean = await getUserMatchmakingStatus(preferences.user.discord_id) !== "not_found";
    if (receiptExists) {
      res.status(400).json({ message: "User is already in the matchmaking queue" });
      return;
    }
    
    await addMatchmakingRequest(preferences, discord_access_token); 
    res.status(200).json({ message: "Matchmaking request initiated" });
  } catch (error) {
    console.error("Error initiating matchmaking:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const checkMatchmakingStatus = async (req: Request, res: Response): Promise<void> => {
  const { discord_id } = req.params;

  try {
    const status = await getUserMatchmakingStatus(discord_id);

    if (status === "in_progress") {
      res.status(200).json({ message: "Matchmaking in progress"});
    } else if (status === "timed_out") {
      removeMatchmakingStatus(discord_id);
      res.status(200).json({ message: "Matchmaking timed out"});
    } else if (status === "group_found") {
      removeMatchmakingStatus(discord_id);
      res.status(200).json({ message: "Group found"});
    } else {
      res.status(404).json({ message: "Matchmaking not in progress" });
    }
    
  } catch (error) {
    console.error("Error checking matchmaking status:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};