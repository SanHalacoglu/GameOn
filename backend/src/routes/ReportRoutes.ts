import express from "express";
import { protectEndpoint } from "../controllers/AuthController";
import {
  getReports,
  getReportById,
  createReport,
  resolveReport,
  deleteReport,
} from "../controllers/ReportController";

const router = express.Router();

router.get("/", protectEndpoint, getReports);
router.get("/:id", protectEndpoint, getReportById);
router.post("/", protectEndpoint, createReport);
router.put("/:id/resolve", protectEndpoint, resolveReport);
router.delete("/:id", protectEndpoint, deleteReport);

export default router;