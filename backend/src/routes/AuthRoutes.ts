import express, { Request, Response, Router } from "express";
import dotenv from "dotenv";
import { handleLoginOrRedirect, handleDiscordCallback, handleRegister } from "../controllers/AuthController";

dotenv.config();

const authRouter: Router = Router();

authRouter.get("/login", handleLoginOrRedirect);
authRouter.get("/callback_discord", handleDiscordCallback);
authRouter.post("/register", handleRegister)

export default authRouter;