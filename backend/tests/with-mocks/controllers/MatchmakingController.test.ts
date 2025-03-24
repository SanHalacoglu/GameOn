import { Request, Response } from "express";
import { AppDataSource } from "../../../src/data-source";
import { Preferences } from "../../../src/entity/Preference";
import { User } from "../../../src/entity/User";
import { Game } from "../../../src/entity/Game";
import { initiateMatchmaking, checkMatchmakingStatus } from "../../../src/controllers/MatchmakingController";
import { addMatchmakingRequest, getUserMatchmakingStatus } from "../../../src/services/MatchmakingService";

// Mock the entire data-source module
jest.mock("../../../src/data-source", () => ({
  AppDataSource: {
    getRepository: jest.fn(),
  },
}));

// Mock the MatchmakingService
jest.mock("../../../src/services/MatchmakingService");

// Mock the repositories
const mockUserRepository = {
  findOne: jest.fn(),
  create: jest.fn(),
  save: jest.fn(),
  delete: jest.fn(),
};

const mockPreferencesRepository = {
  findOne: jest.fn(),
  create: jest.fn(),
  save: jest.fn(),
  delete: jest.fn(),
};

const mockGameRepository = {
  findOne: jest.fn(),
  create: jest.fn(),
  save: jest.fn(),
  delete: jest.fn(),
};

// Mock the getRepository method to return the appropriate mock repository
(AppDataSource.getRepository as jest.Mock).mockImplementation((entity) => {
  if (entity === User) {
    return mockUserRepository;
  } else if (entity === Preferences) {
    return mockPreferencesRepository;
  } else if (entity === Game) {
    return mockGameRepository;
  }
});

let req: Partial<Request>;
let res: Partial<Response>;
let json: jest.Mock;
let status: jest.Mock;

// Setup before each test
beforeEach(() => {
  // Reset mocks before each test
  jest.clearAllMocks();

  // Mock request and response objects
  req = {};
  json = jest.fn();
  status = jest.fn().mockReturnValue({ json });
  res = { status, json };
});

// Input: Non-existent preference ID
// Expected status code: 404
// Expected behavior: Returns a "Preferences not found" message
// Expected output: { message: "Preferences not found" }
it("should return 404 if preferences are not found", async () => {
  req.session = {
    id: "test_session_id",
    cookie: {
      originalMaxAge: 3600,
      path: "/",
      httpOnly: true,
      secure: false,
      sameSite: "lax",
    },
    regenerate: jest.fn(),
    destroy: jest.fn(),
    reload: jest.fn(),
    save: jest.fn(),
    touch: jest.fn(),
    resetMaxAge: jest.fn(),
    user: {
      discord_id: "test_user_123",
      discord_access_token: "test_token",
      discord_refresh_token: "test_refresh_token",
      discord_username: "test_user",
      discord_email: "test@example.com",
      temp_session: false,
    },
  };
  req.body = { preference_id: "999" }; // Non-existent preference ID

  // Mock Preferences repository to return null
  mockPreferencesRepository.findOne.mockResolvedValue(null);

  await initiateMatchmaking(req as Request, res as Response);

  expect(status).toHaveBeenCalledWith(404);
  expect(json).toHaveBeenCalledWith({ message: "Preferences not found" });
});

// Input: User already in the matchmaking queue
// Expected status code: 400
// Expected behavior: Returns a "User is already in the matchmaking queue" message
// Expected output: { message: "User is already in the matchmaking queue" }
it("should return 400 if user is already in the matchmaking queue", async () => {
  req.session = {
    id: "test_session_id",
    cookie: {
      originalMaxAge: 3600,
      path: "/",
      httpOnly: true,
      secure: false,
      sameSite: "lax",
    },
    regenerate: jest.fn(),
    destroy: jest.fn(),
    reload: jest.fn(),
    save: jest.fn(),
    touch: jest.fn(),
    resetMaxAge: jest.fn(),
    user: {
      discord_id: "test_user_123",
      discord_access_token: "test_token",
      discord_refresh_token: "test_refresh_token",
      discord_username: "test_user",
      discord_email: "test@example.com",
      temp_session: false,
    },
  };
  req.body = { preference_id: "1" };

  // Mock Preferences repository to return mockPreferences
  const mockPreferences = {
    preference_id: 1,
    spoken_language: "English",
    time_zone: "UTC",
    skill_level: "casual",
    user: {
      discord_id: "test_user_123",
    },
    game: {
      game_id: 1,
    },
  };
  mockPreferencesRepository.findOne.mockResolvedValue(mockPreferences);

  // Mock getUserMatchmakingStatus to return "in_progress"
  (getUserMatchmakingStatus as jest.Mock).mockResolvedValue("in_progress");

  await initiateMatchmaking(req as Request, res as Response);

  expect(status).toHaveBeenCalledWith(400);
  expect(json).toHaveBeenCalledWith({ message: "User is already in the matchmaking queue" });
});

// Input: Valid preference ID and user not in the matchmaking queue
// Expected status code: 200
// Expected behavior: Initiates matchmaking and returns a success message
// Expected output: { message: "Matchmaking request initiated" }
it("should initiate matchmaking and return 200 on success", async () => {
  req.session = {
    id: "test_session_id",
    cookie: {
      originalMaxAge: 3600,
      path: "/",
      httpOnly: true,
      secure: false,
      sameSite: "lax",
    },
    regenerate: jest.fn(),
    destroy: jest.fn(),
    reload: jest.fn(),
    save: jest.fn(),
    touch: jest.fn(),
    resetMaxAge: jest.fn(),
    user: {
      discord_id: "test_user_123",
      discord_access_token: "test_token",
      discord_refresh_token: "test_refresh_token",
      discord_username: "test_user",
      discord_email: "test@example.com",
      temp_session: false,
    },
  };
  req.body = { preference_id: "1" };

  // Mock Preferences repository to return mockPreferences
  const mockPreferences = {
    preference_id: 1,
    spoken_language: "English",
    time_zone: "UTC",
    skill_level: "casual",
    user: {
      discord_id: "test_user_123",
    },
    game: {
      game_id: 1,
    },
  };
  mockPreferencesRepository.findOne.mockResolvedValue(mockPreferences);

  // Mock getUserMatchmakingStatus to return "not_found"
  (getUserMatchmakingStatus as jest.Mock).mockResolvedValue("not_found");

  // Mock addMatchmakingRequest
  (addMatchmakingRequest as jest.Mock).mockResolvedValue(undefined);

  await initiateMatchmaking(req as Request, res as Response);

  expect(status).toHaveBeenCalledWith(200);
  expect(json).toHaveBeenCalledWith({ message: "Matchmaking request initiated" });
  expect(addMatchmakingRequest).toHaveBeenCalledWith(mockPreferences, "test_token");
});

// Input: Database error when initiating matchmaking
// Expected status code: 500
// Expected behavior: Returns an internal server error message
// Expected output: { message: "Internal server error" }
it("should return 500 if an error occurs", async () => {
  req.session = {
    id: "test_session_id",
    cookie: {
      originalMaxAge: 3600,
      path: "/",
      httpOnly: true,
      secure: false,
      sameSite: "lax",
    },
    regenerate: jest.fn(),
    destroy: jest.fn(),
    reload: jest.fn(),
    save: jest.fn(),
    touch: jest.fn(),
    resetMaxAge: jest.fn(),
    user: {
      discord_id: "test_user_123",
      discord_access_token: "test_token",
      discord_refresh_token: "test_refresh_token",
      discord_username: "test_user",
      discord_email: "test@example.com",
      temp_session: false,
    },
  };
  req.body = { preference_id: "1" };

  // Mock Preferences repository to throw an error
  mockPreferencesRepository.findOne.mockRejectedValue(new Error("Database error"));

  await initiateMatchmaking(req as Request, res as Response);

  expect(status).toHaveBeenCalledWith(500);
  expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
});

// Input: Valid user ID with matchmaking in progress
// Expected status code: 200
// Expected behavior: Returns the matchmaking status
// Expected output: { message: "Matchmaking in progress" }
it("should return 200 with matchmaking status", async () => {
    req.params = { discord_id: "test_user_123" };
  
    // Mock getUserMatchmakingStatus to return "in_progress"
    (getUserMatchmakingStatus as jest.Mock).mockResolvedValue("in_progress");
  
    await checkMatchmakingStatus(req as Request, res as Response);
  
    expect(status).toHaveBeenCalledWith(200);
    expect(json).toHaveBeenCalledWith({ message: "Matchmaking in progress" });
  });
  
  // Input: Valid user ID with matchmaking completed (group found)
  // Expected status code: 200
  // Expected behavior: Returns the matchmaking status showing group found
  // Expected output: { message: "Group found" }
  it("should return 200 with group found status", async () => {
    req.params = { discord_id: "test_user_123" };
  
    // Mock getUserMatchmakingStatus to return "group_found"
    (getUserMatchmakingStatus as jest.Mock).mockResolvedValue("group_found");
  
    await checkMatchmakingStatus(req as Request, res as Response);
  
    expect(status).toHaveBeenCalledWith(200);
    expect(json).toHaveBeenCalledWith({ message: "Group found" });
  });

// Input: Valid user ID with no matchmaking in progress
// Expected status code: 404
// Expected behavior: Returns a "Matchmaking not in progress" message
// Expected output: { message: "Matchmaking not in progress" }
it("should return 404 if matchmaking is not in progress", async () => {
  req.params = { discord_id: "test_user_123" };

  // Mock getUserMatchmakingStatus to return "not_found"
  (getUserMatchmakingStatus as jest.Mock).mockResolvedValue("not_found");

  await checkMatchmakingStatus(req as Request, res as Response);

  expect(status).toHaveBeenCalledWith(404);
  expect(json).toHaveBeenCalledWith({ message: "Matchmaking not in progress" });
});

// Input: Service error when checking matchmaking status
// Expected status code: 500
// Expected behavior: Returns an internal server error message
// Expected output: { message: "Internal server error" }
it("should return 500 if an error occurs", async () => {
  req.params = { discord_id: "test_user_123" };

  // Mock getUserMatchmakingStatus to throw an error
  (getUserMatchmakingStatus as jest.Mock).mockRejectedValue(new Error("Service error"));

  await checkMatchmakingStatus(req as Request, res as Response);

  expect(status).toHaveBeenCalledWith(500);
  expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
});