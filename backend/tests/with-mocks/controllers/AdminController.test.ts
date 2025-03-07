import { Request, Response } from "express";
import { AppDataSource } from "../../../src/data-source"; // Adjusted path
import { Admin } from "../../../src/entity/Admin"; // Adjusted path
import { User } from "../../../src/entity/User"; // Adjusted path
import {
  getAdmins,
  getAdminById,
  createAdmin,
  updateAdmin,
  deleteAdmin,
} from "../../../src/controllers/AdminController"; // Adjusted path

// Mock the entire data-source module
jest.mock("../../../src/data-source", () => ({
  AppDataSource: {
    getRepository: jest.fn(),
  },
}));

// Mock the repositories
const mockAdminRepository = {
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

// Mock the getRepository method to return the appropriate mock repository
(AppDataSource.getRepository as jest.Mock).mockImplementation((entity) => {
  if (entity === Admin) {
    return mockAdminRepository;
  } else if (entity === User) {
    return mockUserRepository;
  }
});

describe("Admin Controller - 500 Errors (With Mocks)", () => {
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

  // Input: Database error when fetching admins
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching admins fails", async () => {
    mockAdminRepository.find.mockRejectedValue(new Error("Database error"));

    await getAdmins(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when fetching admin by ID
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching admin by ID fails", async () => {
    req.params = { id: "1" };
    mockAdminRepository.findOne.mockRejectedValue(new Error("Database error"));

    await getAdminById(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when creating an admin
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when creating an admin fails", async () => {
    req.body = { discord_id: "123", permissions: "read" };
    mockUserRepository.findOne.mockResolvedValue({ discord_id: "123" });
    mockAdminRepository.save.mockRejectedValue(new Error("Database error"));

    await createAdmin(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when updating an admin
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when updating an admin fails", async () => {
    req.params = { id: "1" };
    mockAdminRepository.findOne.mockRejectedValue(new Error("Database error"));

    await updateAdmin(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // Input: Database error when deleting an admin
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when deleting an admin fails", async () => {
    req.params = { id: "1" };
    mockAdminRepository.findOne.mockRejectedValue(new Error("Database error"));

    await deleteAdmin(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });
});