import express, { Request, Response, Router } from "express";
import dotenv from "dotenv";
import { handleLoginOrRedirect, handleDiscordCallback, handleRegister } from "../controllers/AuthController";

dotenv.config();

const authRouter: Router = Router();

authRouter.get("/login", handleLoginOrRedirect);
authRouter.get("/callback_discord", handleDiscordCallback);
authRouter.post("/register", handleRegister)

authRouter.get("/redirect", (req, res) => {
    res.redirect(`intent://redirect?code=${req.query.code}#Intent;scheme=gameoncpen;package=com.example.gameon;end;`)
})

export default authRouter;