import e, { Request, Response } from "express";
import axios from "axios";
import dotenv from "dotenv";


dotenv.config();

declare module 'express-session' {
  interface SessionData {
    user?: {
      discord_id: string;
      discord_access_token: string;
      discord_refresh_token: string;
      discord_username?: string;
      discord_email?: string;
      temp_session: boolean;
    };
  }
}

const DISCORD_AUTH_URL = "https://discord.com/api/oauth2/authorize";
const DISCORD_TOKEN_URL = "https://discord.com/api/oauth2/token";
const DISCORD_USER_URL = "https://discord.com/api/users/@me";
const DISCORD_CLIENT_ID = process.env.DISCORD_CLIENT_ID || "";
const DISCORD_CLIENT_SECRET = process.env.DISCORD_CLIENT_SECRET || "";
const DISCORD_REDIRECT_URI = process.env.DISCORD_REDIRECT_URI || "";

const DB_SERVICE_URL = process.env.DB_SERVICE_URL || "";

const FRONT_END_URL = process.env.FRONTEND_URL || "";

/**
 * Creates URL search parameters for exchanging the auth code.
 * @param code - The authorization code received from Discord.
 * @returns URLSearchParams
 */
function createSearchParamsAuthToken(code: string): URLSearchParams {
  return new URLSearchParams({
    grant_type: "authorization_code",
    code: code,
    redirect_uri: DISCORD_REDIRECT_URI,
    client_id: DISCORD_CLIENT_ID,
    client_secret: DISCORD_CLIENT_SECRET,
  });
}

/**
 * Exchanges the Discord authorization code for an access token.
 * @param code - The authorization code.
 * @returns A promise with token data.
 */
export async function exchangeCodeForAccessToken(code: string): Promise<any> {
  const params = createSearchParamsAuthToken(code);
  const response = await axios.post(DISCORD_TOKEN_URL, params.toString(), {
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
  });
  return response.data;
}

/**
 * Fetches Discord user data using the provided access token.
 * @param accessToken - The access token.
 * @returns A promise with user data.
 */
export async function fetchDiscordUser(accessToken: string): Promise<any> {
  const response = await axios.get(DISCORD_USER_URL, {
    headers: { Authorization: `Bearer ${accessToken}` },
  });
  return response.data;
}

/**
 * 
 * 1. Checks if a session already exists.
 *    - If a session exists:
 *      - Determines the session type:
 *        - If the session indicates a logged-in user, fetch their profile from the database.
 *        - If the session is temporary (user needs to register), redirect them to the frontend registration page.
 * 
 * 2. If no session exists:
 *    - Redirects the client to Discord's OAuth2 login.
 *    - After successful authentication:
 *      - Checks if the user exists in the database.
 *        - If the user exists, create a session for them and log them in.
 *        - If the user does not exist, create a temporary session indicating registration is needed and redirect them to the frontend registration page.
 */
export async function handleLoginOrRedirect(req: Request, res: Response): Promise<void> {
  if (req.session.user){
    if(req.session.user.temp_session){
      console.log("User has a temp session. Redirecting to register page.");
      //TODO FRONT END: CHANGE NAVIGATION LOGIC TO REGISTER PAGE FRONTEND
      res.redirect(`${FRONT_END_URL}/register.html`);
      return

    }else{
      console.log("User has a permanent session. Redirecting to home page.");
      const response = await axios.get(`${DB_SERVICE_URL}/users/${req.session.user.discord_id}`, {
        responseType: 'json'
      });
      
      if(response.status == 200){
        const userProfileData = response.data
        userProfileData.is_registered = true
      
        //TODO FRONT END: CHANGE NAVIGATION LOGIC TO LOGIN/USERPAGE
        res.send(userProfileData);
      }else{
        console.log("Something has went horribly wrong.");
        res.status(response.status).send(response.data);
      }

      return;
    }
  }
  console.log("No Session User");

  const authURL = `${DISCORD_AUTH_URL}?client_id=${DISCORD_CLIENT_ID}&redirect_uri=${DISCORD_REDIRECT_URI}&response_type=code&scope=identify%20email`;
  //TODO FRONT END: KEEP OR CHANGE TO COMMUNICATE WITH DISCORD
  res.redirect(authURL);
  return;
}

/**
 * Handles the Discord OAuth2 callback.
 * 
 * 1. Exchanges the authorization code for an access token.
 * 2. Fetches the user's profile from Discord.
 * 3. Checks if the user exists in the database:
 *    - If yes, creates a session and logs them in.
 *    - If no, creates a temporary session for registration.
 * 4. Sends the user data or redirects as needed.
 */
export async function handleDiscordCallback(req: Request, res: Response): Promise<void> {
  const code = req.query.code as string;
  if (!code) {
    console.log("Something has went wrong with the discord auth code.");
    res.status(400).send("Error fetching auth code");
    return;
  }

  try {
    const tokenData = await exchangeCodeForAccessToken(code);
    console.log("Token Data:", tokenData);

    if (tokenData.error) {
      console.log("Something has went wrong with the Discord token exchange code.");
      res.status(400).send(tokenData.error);
      return;
    }

    const discordUserData = await fetchDiscordUser(tokenData.access_token);
    console.log("User Data:", discordUserData);
    if (!discordUserData.id) {
      res.status(400).send("Error fetching user data");
      return;
    }

    const dbResponse = await axios.get(`${DB_SERVICE_URL}/users/${discordUserData.id}`, {
      responseType: 'json',
      validateStatus: (status) => status < 500
    });
    
    //User Profile Does Not Exist, Create Temp Session Return User To Registration
    if(dbResponse.status == 404){
      req.session.user = {
        discord_id: discordUserData.id,
        discord_access_token: tokenData.access_token,
        discord_refresh_token: tokenData.refresh_token,
        discord_email: discordUserData.email,
        discord_username: discordUserData.username,
        temp_session: true
      }

      //TODO FRONT END: CHANGE NAVIGATION LOGIC TO REGISTER PAGE FRONTEND
      res.redirect(`${FRONT_END_URL}/register.html`);
      return;
      
      //User Profile Exists, Create Session Return User Profile
    }else if(dbResponse.status == 200){
      req.session.user = {
        discord_id: discordUserData.id,
        discord_access_token: tokenData.access_token,
        discord_refresh_token: tokenData.refresh_token,
        temp_session: false
      }

      const userProfileData = dbResponse.data
      userProfileData.is_registered = true

      //TODO FRONT END: CHANGE NAVIGATION LOGIC TO LOGIN/USERPAGE
      res.send(userProfileData);
    }else{
      res.status(dbResponse.status).send(dbResponse.data);
      return;
    }

    res.send(discordUserData);
  } catch (error) {
    console.error("Error during Discord OAuth process:", error);
    res.status(500).send("Internal Server Error");
  }
}

/**
 * Handles user registration.
 * 
 * 1. Redirects to the homepage if no session user exists.
 * 2. Extracts user data from the request body and attaches Discord info from the session.
 * 3. Sends a request to the database service to create a new user.
 * 4. If successful, updates the session and responds with the user data.
 * 5. Otherwise, forwards the error response.
 */
export async function handleRegister(req: Request, res: Response): Promise<void> {
  console.log("In handle register.");
  if (!req.session.user){
    res.redirect(`${FRONT_END_URL}/index.html`);
    return;
  }

  //TODO: This Requires Changes To The Front End To Send The Correct Data
  console.log("User has session and is in register");
  const userData = req.body;
  userData.discord_id = req.session.user.discord_id;
  userData.discord_email = req.session.user.discord_email;
  userData.discord_username = req.session.user.discord_username;
  console.log(userData);

  const response = await axios.post(`${DB_SERVICE_URL}/users`, userData, {
    responseType: 'json'
  });

  console.log(response)

  if(response.status == 201){
    req.session.user.temp_session = false;
    //TODO FRONT END: CHANGE NAVIGATION LOGIC TO LOGIN/USERPAGE
    res.send(response.data);
  }else{
    res.status(response.status).send(response.data);
  }
}