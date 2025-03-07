import { Request, Response } from "express";
import { AppDataSource } from "../../../src/data-source";
import { Report } from "../../../src/entity/Report";
import { User } from "../../../src/entity/User";
import { Group } from "../../../src/entity/Group";
import {
  getReports,
  getReportById,
  createReport,
  resolveReport,
  deleteReport,
} from "../../../src/controllers/ReportController";
import axios from "axios";
import { Session, SessionData } from "express-session";

// Mock the entire data-source module
jest.mock("../../../src/data-source", () => ({
  AppDataSource: {
    getRepository: jest.fn(),
  },
}));

// Mock axios
jest.mock("axios");

// Mock the repositories
const mockReportRepository = {
  find: jest.fn(),
  findOne: jest.fn(),
  create: jest.fn(),
  save: jest.fn(),
  remove: jest.fn(),
};

const mockUserRepository = {
  findOne: jest.fn(),
};

const mockGroupRepository = {
  findOne: jest.fn(),
};

// Mock the getRepository method to return the appropriate mock repository
(AppDataSource.getRepository as jest.Mock).mockImplementation((entity) => {
  if (entity === Report) {
    return mockReportRepository;
  } else if (entity === User) {
    return mockUserRepository;
  } else if (entity === Group) {
    return mockGroupRepository;
  }
});

// Define DB_SERVICE_URL for testing
const DB_SERVICE_URL = "";

// Group: Report Controller - Mocked Tests
describe("Report Controller - Mocked Tests", () => {
  let req: Partial<Request>;
  let res: Partial<Response>;
  let json: jest.Mock;
  let status: jest.Mock;
  let send: jest.Mock;

  // Setup before each test
  beforeEach(() => {
    req = {
      session: {
        id: "mock-session-id",
        cookie: {
          originalMaxAge: 3600,
          expires: new Date(),
          secure: false,
          httpOnly: true,
          path: "/",
          sameSite: "lax",
        },
        regenerate: jest.fn((callback: (err?: any) => void) => {
          callback();
          return req.session as Session & Partial<SessionData>;
        }),
        destroy: jest.fn((callback: (err?: any) => void) => {
          callback();
          return req.session as Session & Partial<SessionData>;
        }),
        reload: jest.fn((callback: (err?: any) => void) => {
          callback();
          return req.session as Session & Partial<SessionData>;
        }),
        save: jest.fn((callback?: (err?: any) => void) => {
          callback?.();
          return req.session as Session & Partial<SessionData>;
        }),
        touch: jest.fn(),
        resetMaxAge: jest.fn(() => req.session as Session & Partial<SessionData>),
        user: {
          discord_id: "123",
          discord_access_token: "access_token",
          discord_refresh_token: "refresh_token",
          temp_session: false,
        },
      } as Session & Partial<SessionData>,
    };
    json = jest.fn();
    status = jest.fn().mockReturnValue({ json });
    send = jest.fn();
    res = { status, json, send };
    jest.spyOn(console, "error").mockImplementation(() => {}); // Suppress console.error logs
  });

  // Clear all mocks after each test
  afterEach(() => {
    jest.clearAllMocks();
    jest.restoreAllMocks();
  });

  // --------------------------
  // getReports
  // --------------------------

  // Input: Database error when fetching reports
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching reports fails", async () => {
    mockReportRepository.find.mockRejectedValue(new Error("Database error"));

    await getReports(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // --------------------------
  // getReportById
  // --------------------------

  // Input: Valid request to fetch a report by ID
  // Expected status code: 200
  // Expected behavior: Returns the requested report
  // Expected output: Report object
  it("should return 200 and the requested report", async () => {
    const mockReport = { report_id: 1, reason: "Test reason" };
    req.params = { id: "1" };
    mockReportRepository.findOne.mockResolvedValue(mockReport);

    await getReportById(req as Request, res as Response);

    expect(mockReportRepository.findOne).toHaveBeenCalledWith({
      where: { report_id: 1 },
      relations: ["reporter", "reported_user", "group"],
    });
    expect(status).toHaveBeenCalledWith(200);
    expect(json).toHaveBeenCalledWith(mockReport);
  });

  // Input: Report not found
  // Expected status code: 404
  // Expected behavior: Returns a not found error message
  // Expected output: { message: "Report not found" }
  it("should return 404 if report is not found", async () => {
    req.params = { id: "1" };
    mockReportRepository.findOne.mockResolvedValue(null);

    await getReportById(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(404);
    expect(json).toHaveBeenCalledWith({ message: "Report not found" });
  });

  // Input: Database error when fetching report by ID
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when fetching report by ID fails", async () => {
    req.params = { id: "1" };
    mockReportRepository.findOne.mockRejectedValue(new Error("Database error"));

    await getReportById(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // --------------------------
  // createReport
  // --------------------------

  // Input: Valid request to create a new report
  // Expected status code: 201
  // Expected behavior: Creates a new report and returns it
  // Expected output: Created report object
  it("should return 201 and the created report", async () => {
    req.body = { reported_discord_id: "456", group_id: 1, reason: "Test reason" };
    const mockReport = { report_id: 1, reason: "Test reason" };
    mockUserRepository.findOne.mockResolvedValue({ discord_id: "123" }); // Reporter
    mockUserRepository.findOne.mockResolvedValueOnce({ discord_id: "456" }); // Reported user
    mockGroupRepository.findOne.mockResolvedValue({ group_id: 1 }); // Group
    mockReportRepository.create.mockReturnValue(mockReport);
    mockReportRepository.save.mockResolvedValue(mockReport);

    await createReport(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(201);
    expect(json).toHaveBeenCalledWith(mockReport);
  });

  // Input: Missing required fields in the request body
  // Expected status code: 400
  // Expected behavior: Returns a bad request error message
  // Expected output: { message: "All fields are required" }
  it("should return 400 if required fields are missing", async () => {
    req.body = { reported_discord_id: "456", group_id: 1 }; // Missing reason

    await createReport(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(400);
    expect(json).toHaveBeenCalledWith({ message: "All fields are required" });
  });

  // Input: Reporter not found in the database
  // Expected status code: 404
  // Expected behavior: Returns a not found error message
  // Expected output: { message: "Reporter not found" }
  it("should return 404 if reporter is not found", async () => {
    req.body = { reported_discord_id: "456", group_id: 1, reason: "Test reason" };
    mockUserRepository.findOne.mockResolvedValue(null); // Reporter not found

    await createReport(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(404);
    expect(json).toHaveBeenCalledWith({ message: "Reporter not found" });
  });

  // Input: Database error when creating a report
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when creating a report fails", async () => {
    req.body = { reported_discord_id: "456", group_id: 1, reason: "Test reason" };
    mockUserRepository.findOne.mockResolvedValue({ discord_id: "123" }); // Reporter
    mockUserRepository.findOne.mockResolvedValueOnce({ discord_id: "456" }); // Reported user
    mockGroupRepository.findOne.mockResolvedValue({ group_id: 1 }); // Group
    mockReportRepository.save.mockRejectedValue(new Error("Database error"));

    await createReport(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // --------------------------
  // resolveReport
  // --------------------------

  // Input: Valid request to resolve a report
  // Expected status code: 200
  // Expected behavior: Resolves the report and updates the database
  // Expected output: Resolved report object
  it("should return 200 and the resolved report", async () => {
    req.params = { id: "123" };
    req.query = { ban: "true" };
    const mockReport = { report_id: 123, resolved: false, reported_user: { discord_id: "456" } };
    mockReportRepository.findOne.mockResolvedValue(mockReport);
    mockReportRepository.save.mockResolvedValue({ ...mockReport, resolved: true });
    (axios.put as jest.Mock).mockResolvedValue({});

    await resolveReport(req as Request, res as Response);

    expect(mockReportRepository.findOne).toHaveBeenCalledWith({
      where: { report_id: 123 },
      relations: ["reported_user"],
    });
    expect(mockReportRepository.save).toHaveBeenCalledWith({ ...mockReport, resolved: true });
    expect(axios.put).toHaveBeenCalledWith(`${DB_SERVICE_URL}/users/456/ban`, {
      responseType: "json",
    });
    expect(status).toHaveBeenCalledWith(200);
    expect(json).toHaveBeenCalledWith({ ...mockReport, resolved: true });
  });

  // Input: Report not found in the database
  // Expected status code: 404
  // Expected behavior: Returns a not found error message
  // Expected output: { message: "Report not found" }
  it("should return 404 if report is not found", async () => {
    req.params = { id: "123" };
    mockReportRepository.findOne.mockResolvedValue(null);

    await resolveReport(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(404);
    expect(json).toHaveBeenCalledWith({ message: "Report not found" });
  });

  // Input: Database error when resolving a report
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when resolving a report fails", async () => {
    req.params = { id: "123" };
    mockReportRepository.findOne.mockRejectedValue(new Error("Database error"));

    await resolveReport(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });

  // --------------------------
  // deleteReport
  // --------------------------

  // Input: Report not found in the database
  // Expected status code: 404
  // Expected behavior: Returns a not found error message
  // Expected output: { message: "Report not found" }
  it("should return 404 if report is not found", async () => {
    req.params = { id: "123" };
    mockReportRepository.findOne.mockResolvedValue(null);

    await deleteReport(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(404);
    expect(json).toHaveBeenCalledWith({ message: "Report not found" });
  });

  // Input: Database error when deleting a report
  // Expected status code: 500
  // Expected behavior: Returns an internal server error message
  // Expected output: { message: "Internal server error" }
  it("should return 500 when deleting a report fails", async () => {
    req.params = { id: "123" };
    mockReportRepository.findOne.mockRejectedValue(new Error("Database error"));

    await deleteReport(req as Request, res as Response);

    expect(status).toHaveBeenCalledWith(500);
    expect(json).toHaveBeenCalledWith({ message: "Internal server error" });
  });
});