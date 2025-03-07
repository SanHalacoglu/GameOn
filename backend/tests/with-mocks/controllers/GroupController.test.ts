import { Request, Response } from "express";
import { Session, SessionData } from "express-session"; // Import Session and SessionData
import { AppDataSource } from "../../../src/data-source";
import { Group } from "../../../src/entity/Group";
import { Game } from "../../../src/entity/Game";
import { User } from "../../../src/entity/User";
import { GroupMember } from "../../../src/entity/GroupMember";
import {
  getGroups,
  getGroupById,
  createGroup,
  updateGroup,
  deleteGroup,
  joinGroup,
  leaveGroup,
  getGroupMembers,
  getGroupUrl,
} from "../../../src/controllers/GroupController";

// Mock the entire data-source module
jest.mock("../../../src/data-source", () => ({
  AppDataSource: {
    getRepository: jest.fn(),
  },
}));

// Mock the repositories
const mockGroupRepository = {
  find: jest.fn(),
  findOne: jest.fn(),
  create: jest.fn(),
  save: jest.fn(),
  merge: jest.fn(),
  remove: jest.fn(),
};

const mockGameRepository = {
  findOne: jest.fn(),
};

const mockUserRepository = {
  findOne: jest.fn(),
};

const mockGroupMemberRepository = {
  find: jest.fn(),
  findOne: jest.fn(),
  create: jest.fn(),
  save: jest.fn(),
  remove: jest.fn(),
};

// Mock the getRepository method to return the appropriate mock repository
(AppDataSource.getRepository as jest.Mock).mockImplementation((entity) => {
  if (entity === Group) {
    return mockGroupRepository;
  } else if (entity === Game) {
    return mockGameRepository;
  } else if (entity === User) {
    return mockUserRepository;
  } else if (entity === GroupMember) {
    return mockGroupMemberRepository;
  }
});

// Group: Group Controller - 500 Errors
describe("Group Controller - 500 Errors", () => {
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

  // Input: Database error when fetching all groups
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching groups fails", async () => {
    mockGroupRepository.find.mockRejectedValue(new Error("Database error"));

    await getGroups(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when fetching group by ID
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching group by ID fails", async () => {
    req.params = { id: "1" };
    mockGroupRepository.findOne.mockRejectedValue(new Error("Database error"));

    await getGroupById(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when creating a group
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when creating a group fails", async () => {
    req.body = {
      game_id: "1",
      group_name: `Test Group ${Date.now()}`,
      max_players: 5,
    };
    mockGameRepository.findOne.mockResolvedValue({});
    mockGroupRepository.save.mockRejectedValue(new Error("Database error"));

    await createGroup(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when updating a group
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when updating a group fails", async () => {
    req.params = { id: "1" };
    req.body = { group_name: `Updated Group ${Date.now()}` };
    mockGroupRepository.findOne.mockResolvedValue({});
    mockGroupRepository.save.mockRejectedValue(new Error("Database error"));

    await updateGroup(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when deleting a group
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when deleting a group fails", async () => {
    req.params = { id: "1" };
    mockGroupRepository.findOne.mockResolvedValue({});
    mockGroupRepository.remove.mockRejectedValue(new Error("Database error"));

    await deleteGroup(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when joining a group
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when joining a group fails", async () => {
    req.params = { group_id: "1" };
    req.body = { discord_id: "123" };
    mockGroupRepository.findOne.mockResolvedValue({});
    mockUserRepository.findOne.mockResolvedValue({});
    mockGroupMemberRepository.save.mockRejectedValue(new Error("Database error"));

    await joinGroup(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when leaving a group
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when leaving a group fails", async () => {
    req.params = { id: "1" };
    req.body = { discord_id: "123" };
    mockGroupMemberRepository.findOne.mockResolvedValue({});
    mockGroupMemberRepository.remove.mockRejectedValue(new Error("Database error"));

    await leaveGroup(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when fetching group members
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching group members fails", async () => {
    req.params = { id: "1" };
    req.session = {
      user: {
        discord_id: "123",
        discord_access_token: "access_token",
        discord_refresh_token: "refresh_token",
        temp_session: false,
      },
      id: "mock-session-id",
      cookie: {
        path: "/",
        _expires: null,
        originalMaxAge: null,
        httpOnly: true,
        secure: false,
        sameSite: undefined,
      },
      regenerate: jest.fn(),
      destroy: jest.fn(),
      reload: jest.fn(),
      save: jest.fn(),
      touch: jest.fn(),
    } as unknown as Session & Partial<SessionData>; // Use imported SessionData
    mockGroupMemberRepository.find.mockRejectedValue(new Error("Database error"));

    await getGroupMembers(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when fetching group URL
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching group URL fails", async () => {
    req.params = { id: "1" };
    mockGroupRepository.findOne.mockRejectedValue(new Error("Database error"));

    await getGroupUrl(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });
});