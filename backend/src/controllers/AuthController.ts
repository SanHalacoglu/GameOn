import { Request, Response } from "express";
import axios from "axios";

const DISCORD_AUTH_URL = "https://discord.com/api/oauth2/authorize";
const DISCORD_TOKEN_URL = "https://discord.com/api/oauth2/token";
const DISCORD_USER_URL = "https://discord.com/api/users/@me";
const DISCORD_CLIENT_ID = process.env.DISCORD_CLIENT_ID || "";
const DISCORD_CLIENT_SECRET = process.env.DISCORD_CLIENT_SECRET || "";
const DISCORD_REDIRECT_URI = process.env.DISCORD_REDIRECT_URI || "";

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
 * Handles the login redirect. This function sends the user to Discord's OAuth page.
 */
export function loginRedirect(req: Request, res: Response): void {
  // Optionally add session validations here
  const authURL = `${DISCORD_AUTH_URL}?client_id=${DISCORD_CLIENT_ID}&redirect_uri=${DISCORD_REDIRECT_URI}&response_type=code&scope=identify%20email`;
  res.redirect(authURL);
}

/**
 * Handles the Discord callback. This function exchanges the code for an access token,
 * fetches user data, and then sends it in the response.
 */
export async function handleDiscordCallback(req: Request, res: Response): Promise<void> {
  const code = req.query.code as string;

  if (!code) {
    res.status(400).send("Error fetching auth code");
    return;
  }

  try {
    const tokenData = await exchangeCodeForAccessToken(code);
    console.log("Token Data:", tokenData);

    if (tokenData.error) {
      res.status(400).send(tokenData.error);
      return;
    }

    const userData = await fetchDiscordUser(tokenData.access_token);
    console.log("User Data:", userData);

    // TODO: Check if user is registered in your system,
    // if not redirect them to a registration page
    res.send(userData);
  } catch (error) {
    console.error("Error during Discord OAuth process:", error);
    res.status(500).send("Internal Server Error");
  }
}