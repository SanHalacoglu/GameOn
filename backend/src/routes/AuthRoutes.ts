import express, { Request, Response, Router } from "express";
import dotenv from "dotenv";
import { loginRedirect, handleDiscordCallback } from "../controllers/AuthController";

dotenv.config();

const authRouter: Router = Router();

authRouter.get("/login", loginRedirect);
authRouter.get("/callback_discord", handleDiscordCallback);

export default authRouter;