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

router.get("/",  getReports);
router.get("/:id",  getReportById);
router.post("/",  createReport);
router.put("/:id/resolve", resolveReport);
router.delete("/:id", deleteReport);

export default router;