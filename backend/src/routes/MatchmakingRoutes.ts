import express from "express";
import { initiateMatchmaking, checkMatchmakingStatus } from "../controllers/MatchmakingController";
import { protectEndpoint } from "../controllers/AuthController";

const router = express.Router();

//TODO: Add endpoint security to the routes
router.post("/initiate", initiateMatchmaking);
router.get("/status/:discord_id", checkMatchmakingStatus);

export default router;