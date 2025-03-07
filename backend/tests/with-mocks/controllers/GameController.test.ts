import { Request, Response } from "express";
import { AppDataSource } from "../../../src/data-source";
import { Game } from "../../../src/entity/Game";
import {
  getGames,
  getGameById,
  createGame,
  updateGame,
  deleteGame,
} from "../../../src/controllers/GameController";

// Mock the entire data-source module
jest.mock("../../../src/data-source", () => ({
  AppDataSource: {
    getRepository: jest.fn(),
  },
}));

// Mock the Game repository methods
const mockGameRepository = {
  find: jest.fn(),
  findOne: jest.fn(),
  create: jest.fn(),
  save: jest.fn(),
  merge: jest.fn(),
  remove: jest.fn(),
};

// Mock the getRepository method to return the mockGameRepository
(AppDataSource.getRepository as jest.Mock).mockReturnValue(mockGameRepository);

describe("Game Controller - 500 Errors", () => {
  let req: Partial<Request>;
  let res: Partial<Response>;
  let json: jest.Mock;
  let status: jest.Mock;

  beforeEach(() => {
    req = {};
    json = jest.fn();
    status = jest.fn().mockReturnValue({ json });
    res = { status, json };
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  // Input: Database error when fetching all games
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching games fails", async () => {
    // Simulate a database error
    mockGameRepository.find.mockRejectedValue(new Error("Database error"));

    await getGames(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when fetching a game by ID
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching game by ID fails", async () => {
    req.params = { id: "1" };
    // Simulate a database error
    mockGameRepository.findOne.mockRejectedValue(new Error("Database error"));

    await getGameById(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when creating a game
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when creating a game fails", async () => {
    req.body = { game_name: "New Game" };
    // Simulate no existing game
    mockGameRepository.findOne.mockResolvedValue(null);
    // Simulate a database error during save
    mockGameRepository.save.mockRejectedValue(new Error("Database error"));

    await createGame(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when updating a game
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when updating a game fails", async () => {
    req.params = { id: "1" };
    req.body = { game_name: "Updated Game" };
    // Simulate finding a game
    mockGameRepository.findOne.mockResolvedValue({});
    // Simulate a database error during save
    mockGameRepository.save.mockRejectedValue(new Error("Database error"));

    await updateGame(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when deleting a game
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when deleting a game fails", async () => {
    req.params = { id: "1" };
    // Simulate finding a game
    mockGameRepository.findOne.mockResolvedValue({});
    // Simulate a database error during remove
    mockGameRepository.remove.mockRejectedValue(new Error("Database error"));

    await deleteGame(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });
});