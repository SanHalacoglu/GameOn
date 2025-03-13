import { Request, Response } from "express";
import {
  createReport,
} from "../../src/controllers/ReportController";
import { Session, SessionData } from "express-session";

// Group: Report Controller - Mocked Tests
describe("Report Controller - Mocked Tests", () => {
  let req: Partial<Request>;
  let res: Partial<Response>;
  let json: jest.Mock;
  let status: jest.Mock;

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
    res = { status, json};
  });

  // Clear all mocks after each test
  afterEach(() => {
    jest.clearAllMocks();
    jest.restoreAllMocks();
  });

  it("Report reason must be less than 500 characters.", async () => {
    req.body = { reported_discord_id: "456", group_id: 1, reason: "a".repeat(600) };
    const mockReport = { report_id: 1, reason: "Test reason" };

    await createReport(req as Request, res as Response);

    expect(res.status).toHaveBeenCalledWith(400);
    expect(res.json).toHaveBeenCalledWith({ message: "Report reason must be less than 500 characters" });
  });
});