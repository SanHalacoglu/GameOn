import express from "express";
import { initiateMatchmaking, checkMatchmakingStatus } from "../controllers/MatchmakingController";

const router = express.Router();

router.post("/initiate", initiateMatchmaking);
router.get("/status/:discord_id", checkMatchmakingStatus);

export default router;