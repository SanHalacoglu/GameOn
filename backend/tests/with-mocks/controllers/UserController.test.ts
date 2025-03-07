import { Request, Response } from "express";
import { AppDataSource } from "../../../src/data-source";
import { User } from "../../../src/entity/User";
import { GroupMember } from "../../../src/entity/GroupMember";
import {
  getUsers,
  getUserById,
  createUser,
  updateUser,
  deleteUser,
  getUserGroups,
  banUser,
} from "../../../src/controllers/UserController";

// Mock the entire data-source module
jest.mock("../../../src/data-source", () => ({
  AppDataSource: {
    getRepository: jest.fn(),
  },
}));

// Mock the repositories
const mockUserRepository = {
  find: jest.fn(),
  findOne: jest.fn(),
  create: jest.fn(),
  save: jest.fn(),
  merge: jest.fn(),
  remove: jest.fn(),
};

const mockGroupMemberRepository = {
  find: jest.fn(),
};

// Mock the getRepository method to return the appropriate mock repository
(AppDataSource.getRepository as jest.Mock).mockImplementation((entity) => {
  if (entity === User) {
    return mockUserRepository;
  } else if (entity === GroupMember) {
    return mockGroupMemberRepository;
  }
});

// Group: User Controller - Mocked Tests (Failure Paths)
describe("User Controller - Mocked Tests (Failure Paths)", () => {
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

  // Test: Get all users (database error)
  // Input: Database error when fetching users
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching users fails", async () => {
    mockUserRepository.find.mockRejectedValue(new Error("Database error"));

    await getUsers(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Test: Get user by ID (database error)
  // Input: Database error when fetching user by ID
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching user by ID fails", async () => {
    req.params = { id: "123" };
    mockUserRepository.findOne.mockRejectedValue(new Error("Database error"));

    await getUserById(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Test: Create user (database error)
  // Input: Database error when creating a user
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when creating a user fails", async () => {
    const mockUser = { discord_id: "123", username: "test_user", email: "test@example.com" };
    mockUserRepository.findOne.mockResolvedValue(null); // Simulate no existing user
    mockUserRepository.save.mockRejectedValue(new Error("Database error"));
    req.body = mockUser;

    await createUser(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Test: Update user (database error)
  // Input: Database error when updating a user
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when updating a user fails", async () => {
    const mockUser = { discord_id: "123", username: "test_user", email: "test@example.com" };
    mockUserRepository.findOne.mockResolvedValue(mockUser);
    mockUserRepository.save.mockRejectedValue(new Error("Database error"));
    req.params = { id: "123" };
    req.body = { username: "updated_user" };

    await updateUser(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Test: Delete user (database error)
  // Input: Database error when deleting a user
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when deleting a user fails", async () => {
    const mockUser = { discord_id: "123", username: "test_user", email: "test@example.com" };
    mockUserRepository.findOne.mockResolvedValue(mockUser);
    mockUserRepository.remove.mockRejectedValue(new Error("Database error"));
    req.params = { id: "123" };

    await deleteUser(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Test: Get user groups (database error)
  // Input: Database error when fetching user groups
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching user groups fails", async () => {
    req.params = { id: "123" };
    mockGroupMemberRepository.find.mockRejectedValue(new Error("Database error"));

    await getUserGroups(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Test: Ban user (database error)
  // Input: Database error when banning a user
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when banning a user fails", async () => {
    const mockUser = { discord_id: "123", username: "test_user", email: "test@example.com", banned: false };
    mockUserRepository.findOne.mockResolvedValue(mockUser);
    mockUserRepository.save.mockRejectedValue(new Error("Database error"));
    req.params = { id: "123" };

    await banUser(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });
});