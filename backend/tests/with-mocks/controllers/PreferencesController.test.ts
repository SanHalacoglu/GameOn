import { Request, Response } from "express";
import { AppDataSource } from "../../../src/data-source";
import { Preferences } from "../../../src/entity/Preference";
import { User } from "../../../src/entity/User";
import { Game } from "../../../src/entity/Game";
import {
  getPreferences,
  getPreferencesById,
  getPreferencesByUserId,
  createPreferences,
  updatePreferences,
  deletePreferences,
} from "../../../src/controllers/PreferencesController";

// Mock the entire data-source module
jest.mock("../../../src/data-source", () => ({
  AppDataSource: {
    getRepository: jest.fn(),
  },
}));

// Mock the repositories
const mockPreferencesRepository = {
  find: jest.fn(),
  findOne: jest.fn(),
  create: jest.fn(),
  save: jest.fn(),
  merge: jest.fn(),
  remove: jest.fn(),
};

const mockUserRepository = {
  findOne: jest.fn(),
};

const mockGameRepository = {
  findOne: jest.fn(),
};

// Mock the getRepository method to return the appropriate mock repository
(AppDataSource.getRepository as jest.Mock).mockImplementation((entity) => {
  if (entity === Preferences) {
    return mockPreferencesRepository;
  } else if (entity === User) {
    return mockUserRepository;
  } else if (entity === Game) {
    return mockGameRepository;
  }
});

// Group: Preferences Controller - 500 Errors
describe("Preferences Controller - 500 Errors", () => {
  let req: Partial<Request>;
  let res: Partial<Response>;
  let json: jest.Mock;
  let status: jest.Mock;

  // Setup before each test
  beforeEach(() => {
    req = {};
    json = jest.fn();
    status = jest.fn().mockReturnValue({ json });
    res = { status, json };
  });

  // Clear all mocks after each test
  afterEach(() => {
    jest.clearAllMocks();
  });

  // Input: Database error when fetching all preferences
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching preferences fails", async () => {
    mockPreferencesRepository.find.mockRejectedValue(new Error("Database error"));

    await getPreferences(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when fetching preferences by ID
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching preferences by ID fails", async () => {
    req.params = { id: "1" };
    mockPreferencesRepository.findOne.mockRejectedValue(new Error("Database error"));

    await getPreferencesById(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when fetching preferences by user ID
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching preferences by user ID fails", async () => {
    req.params = { userId: "123" };
    mockUserRepository.findOne.mockRejectedValue(new Error("Database error"));

    await getPreferencesByUserId(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when creating preferences
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when creating preferences fails", async () => {
    req.body = {
      discord_id: "123",
      spoken_language: "English",
      time_zone: "UTC",
      skill_level: "Beginner",
      game_id: "1",
    };
    mockUserRepository.findOne.mockResolvedValue({});
    mockGameRepository.findOne.mockResolvedValue({});
    mockPreferencesRepository.save.mockRejectedValue(new Error("Database error"));

    await createPreferences(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when updating preferences
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when updating preferences fails", async () => {
    req.params = { id: "1" };
    req.body = { game_id: "2" };
    mockPreferencesRepository.findOne.mockResolvedValue({});
    mockGameRepository.findOne.mockResolvedValue({});
    mockPreferencesRepository.save.mockRejectedValue(new Error("Database error"));

    await updatePreferences(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when deleting preferences
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when deleting preferences fails", async () => {
    req.params = { id: "1" };
    mockPreferencesRepository.findOne.mockResolvedValue({});
    mockPreferencesRepository.remove.mockRejectedValue(new Error("Database error"));

    await deletePreferences(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });
});