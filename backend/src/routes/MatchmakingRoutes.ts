import express from "express";
import { initiateMatchmaking } from "../controllers/MatchmakingController";

const router = express.Router();

router.post("/initiate", initiateMatchmaking);

export default router;