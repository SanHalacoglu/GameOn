import { Request, Response } from "express";
import axios from "axios";
import dotenv from "dotenv";
import { handleDiscordCallback, handleLoginOrRedirect, handleLogout, handleRegister, protectEndpoint } from "../../../src/controllers/AuthController";

dotenv.config();

// Mock the axios module
jest.mock("axios");
const mockedAxios = axios as jest.Mocked<typeof axios>;

let req: Partial<Request>;
let res: Partial<Response>;

// Setup before each test
beforeEach(() => {
    // Reset mocks before each test
    jest.clearAllMocks();

    // Mock request and response objects
    req = {};

    res = {
        status: jest.fn().mockReturnThis(),
        json: jest.fn(),
        redirect: jest.fn(),
    }

});

function createBaseSession() {
    return {
        id: "test_session_id",
        cookie: {
            originalMaxAge: 3600,
            path: "/",
            httpOnly: true,
            secure: false,
            sameSite: "lax" as const,
        },
        regenerate: jest.fn(),
        destroy: jest.fn(),
        reload: jest.fn(),
        save: jest.fn(),
        touch: jest.fn(),
        resetMaxAge: jest.fn(),
    };
}

function createUserData(tempSession = false) {
    return {
        discord_id: "test_user_123",
        discord_access_token: "test_token",
        discord_refresh_token: "test_refresh_token",
        discord_username: "test_user",
        discord_email: "test@example.com",
        temp_session: tempSession,
    };
}

it("Protect Endpoint. Returns 401 if no session found", async () => {
    req.session = createBaseSession();

    const next = jest.fn();
    await protectEndpoint(req as Request, res as Response, next);

    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith({ message: "Unauthorized. Requires a session to access this endpoint." });
    expect(next).not.toHaveBeenCalled();
});

it("Protect Endpoint. Enables access to appropriate function if session exists.", async () => {
    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };

    const next = jest.fn();
    await protectEndpoint(req as Request, res as Response, next);

    expect(next).toHaveBeenCalled();
});

it("Handle Register. Session Data does not match Body.", async () => {
    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };

    req.body = {
        discord_id: "test_user_456"
    }

    await handleRegister(req as Request, res as Response);
    expect(res.status).toHaveBeenCalledWith(403);
    expect(res.json).toHaveBeenCalledWith({ message: "Discord ID does not match session information." });
});

it("Protect Endpoint. Enables access to appropriate function if session exists.", async () => {
    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };

    const next = jest.fn();
    await protectEndpoint(req as Request, res as Response, next);

    expect(next).toHaveBeenCalled();
});

it("Handle Register. Session Data does not match Body.", async () => {
    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };

    req.body = {
        discord_id: "test_user_4312312312"
    }

    await handleRegister(req as Request, res as Response);
    expect(res.status).toHaveBeenCalledWith(403);
    expect(res.json).toHaveBeenCalledWith({ message: "Discord ID does not match session information." });
});

it("Handle Register. User Post Has Failed.", async () => {
    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };

    req.body = {
        discord_id: "test_user_123"
    }

    mockedAxios.post.mockResolvedValue({ status: 500, data: { message: "Internal server error" } });

    await handleRegister(req as Request, res as Response);

    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.json).toHaveBeenCalledWith({ message: "Internal server error" });
});

it("Handle Register. Preferences Post Has Failed.", async () => {
    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };

    req.body = {
        discord_id: "test_user_123"
    }

    mockedAxios.post.mockResolvedValueOnce({ status: 201 }).mockResolvedValueOnce({ status: 500, data: { message: "Internal server error" } });

    await handleRegister(req as Request, res as Response);

    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.json).toHaveBeenCalledWith({ message: "Internal server error" });
});

it("Handle Register. Register Success.", async () => {
    const user = createUserData();

    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };

    req.body = {
        discord_id: "test_user_123"
    }

    mockedAxios.post
        .mockResolvedValueOnce({
            status: 201, data: {
                discord_id: user.discord_id,
                username: user.discord_username,
                email: user.discord_email,
            }
        })
        .mockResolvedValueOnce({
            status: 201, data: {
                preference_id: 1
            }
        });


    await handleRegister(req as Request, res as Response);

    expect(res.json).toHaveBeenCalledWith({
        discord_id: user.discord_id,
        username: user.discord_username,
        email: user.discord_email,
    });
});

it("Handle LoginOrRedirect. User has a temp session.", async () => {
    const user = createUserData(true);

    req.session = {
        ...createBaseSession(),
        user: createUserData(true),
    };

    await handleLoginOrRedirect(req as Request, res as Response);

    const FRONTEND_PREFERENCES_URL = "gameoncpen://preferences"
    const expectedURI = FRONTEND_PREFERENCES_URL + `?discord_id=${user.discord_id}`
    expect(res.redirect).toHaveBeenCalledWith(expectedURI);
});

it("Handle LoginOrRedirect. User has a permenant session. Get User Fails", async () => {
    const user = createUserData();

    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };

    mockedAxios.get.mockResolvedValueOnce({ status: 500, data: { message: "Internal server error" } });

    await handleLoginOrRedirect(req as Request, res as Response);

    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.json).toHaveBeenCalledWith({ message: "Internal server error" });
});

it("Handle LoginOrRedirect. User has a permenant session. Returning user profile.", async () => {
    const user = createUserData();

    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };

    mockedAxios.get.mockResolvedValueOnce({
        status: 200, data: {
            discord_id: user.discord_id,
            username: user.discord_username,
            email: user.discord_email,

        }
    });

    await handleLoginOrRedirect(req as Request, res as Response);

    expect(res.json).toHaveBeenCalledWith({
        discord_id: user.discord_id,
        username: user.discord_username,
        email: user.discord_email,
    });
});

it("Handle Logout. Successfully logs out user", async () => {
    const user = createUserData();

    req.session = {
        ...createBaseSession(),
        user: createUserData(),
        regenerate: jest.fn().mockImplementation((callback) => callback(null))
    };

    res.sendStatus = jest.fn();

    await handleLogout(req as Request, res as Response);

    expect(res.sendStatus).toHaveBeenCalledWith(204);
});

it("Handle Logout. Fails to regenerate session", async () => {
    const user = createUserData();

    req.session = {
        ...createBaseSession(),
        user: createUserData(),
        regenerate: jest.fn().mockImplementation((callback) => callback(new Error("Failed to regenerate")))
    };

    res.sendStatus = jest.fn();

    await handleLogout(req as Request, res as Response);

    expect(res.sendStatus).toHaveBeenCalledWith(500);
});

it("Handle DiscordCallback. Request does not have auth code", async () => {
    req.query = {};

    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };

    await handleDiscordCallback(req as Request, res as Response);

    expect(res.status).toHaveBeenCalledWith(400);
    expect(res.json).toHaveBeenCalledWith({ message: "Error fetching auth code" });
});

it("Handle DiscordCallback. Exhange Code For Access Token Has Failed.", async () => {
    req.query = { code: "test_code" };

    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };


    mockedAxios.post.mockResolvedValueOnce({ data: { error: "invalid_grant" } });

    await handleDiscordCallback(req as Request, res as Response);

    expect(res.status).toHaveBeenCalledWith(400);
    expect(res.json).toHaveBeenCalledWith("invalid_grant");
});

it("Handle DiscordCallback. Discord Exchange For Token and User Data Has Failed.", async () => {
    req.query = { code: "test_code" };

    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };


    const tokenData = {
        discord_access_token: "test_token",
        discord_refresh_token: "test_refresh_token",
    }

    const discordUserData = {
        id: "test_user_123",
        username: "test_user",
        email: "test@example.com",
    }

    mockedAxios.post.mockResolvedValueOnce({
        data: {
            access_token: tokenData.discord_access_token,
            refresh_token: tokenData.discord_refresh_token,
            token_type: "Bearer",
            expires_in: 604800
        }
    });

    mockedAxios.get.mockResolvedValueOnce({ data: { error: "invalid_grant" } });

    await handleDiscordCallback(req as Request, res as Response);

    expect(res.status).toHaveBeenCalledWith(400);
    expect(res.json).toHaveBeenCalledWith({ message: "Error fetching user data" });
});

it("Handle DiscordCallback. Fetching User From DB Fails.", async () => {
    req.query = { code: "test_code" };

    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };


    const tokenData = {
        discord_access_token: "test_token",
        discord_refresh_token: "test_refresh_token",
    }

    const discordUserData = {
        id: "test_user_123",
        username: "test_user",
        email: "test@example.com",
    }

    mockedAxios.post.mockResolvedValueOnce({
        data: {
            access_token: tokenData.discord_access_token,
            refresh_token: tokenData.discord_refresh_token,
            token_type: "Bearer",
            expires_in: 604800
        }
    });

    mockedAxios.get
    .mockResolvedValueOnce({ data: discordUserData })
    .mockResolvedValueOnce({ status: 500, data: { message: "Internal server error" } });

    await handleDiscordCallback(req as Request, res as Response);

    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.json).toHaveBeenCalledWith({ message: "Internal server error" });
});

it("Handle DiscordCallback. Fetching User From DB Fails.", async () => {
    req.query = { code: "test_code" };

    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };

    const tokenData = {
        discord_access_token: "test_token",
        discord_refresh_token: "test_refresh_token",
    }

    const discordUserData = {
        id: "test_user_123",
        username: "test_user",
        email: "test@example.com",
    }

    mockedAxios.post.mockResolvedValueOnce({
        data: {
            access_token: tokenData.discord_access_token,
            refresh_token: tokenData.discord_refresh_token,
            token_type: "Bearer",
            expires_in: 604800
        }
    });

    mockedAxios.get
    .mockResolvedValueOnce({ data: discordUserData })
    .mockResolvedValueOnce({ status: 500, data: { message: "Internal server error" } });

    await handleDiscordCallback(req as Request, res as Response);

    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.json).toHaveBeenCalledWith({ message: "Internal server error" });
});

it("Handle DiscordCallback. User Profile Does Not Exist, Create Temp Session Return User To Registration.", async () => {
    req.query = { code: "test_code" };

    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };

    const tokenData = {
        discord_access_token: "test_token",
        discord_refresh_token: "test_refresh_token",
    }

    const discordUserData = {
        id: "test_user_123",
        username: "test_user",
        email: "test@example.com",
    }

    mockedAxios.post.mockResolvedValueOnce({
        data: {
            access_token: tokenData.discord_access_token,
            refresh_token: tokenData.discord_refresh_token,
            token_type: "Bearer",
            expires_in: 604800
        }
    });

    mockedAxios.get
    .mockResolvedValueOnce({ data: discordUserData })
    .mockResolvedValueOnce({ status: 404});

    await handleDiscordCallback(req as Request, res as Response);

    const FRONTEND_PREFERENCES_URL = "gameoncpen://preferences"
    const expectedURI = FRONTEND_PREFERENCES_URL + `?discord_id=${discordUserData.id}`
    expect(res.redirect).toHaveBeenCalledWith(expectedURI);
});

it("Handle DiscordCallback. User Profile Exists, Create Session Return User Profile.", async () => {
    req.query = { code: "test_code" };

    req.session = {
        ...createBaseSession(),
        user: createUserData(),
    };

    const tokenData = {
        discord_access_token: "test_token",
        discord_refresh_token: "test_refresh_token",
    }

    const discordUserData = {
        id: "test_user_123",
        username: "test_user",
        email: "test@example.com",
    }

    mockedAxios.post.mockResolvedValueOnce({
        data: {
            access_token: tokenData.discord_access_token,
            refresh_token: tokenData.discord_refresh_token,
            token_type: "Bearer",
            expires_in: 604800
        }
    });

    mockedAxios.get
    .mockResolvedValueOnce({ data: discordUserData })
    .mockResolvedValueOnce({ status: 200, data: discordUserData });

    await handleDiscordCallback(req as Request, res as Response);

    expect(res.json).toHaveBeenCalledWith(discordUserData);
});