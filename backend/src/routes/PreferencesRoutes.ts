import express from "express";
import {
  getPreferences,
  getPreferencesById,
  createPreferences,
  updatePreferences,
  deletePreferences,
  getPreferencesByUserId
} from "../controllers/PreferencesController";

const router = express.Router();

router.get("/", getPreferences);
router.get("/:id", getPreferencesById);
router.post("/", createPreferences);
router.put("/:id", updatePreferences);
router.delete("/:id", deletePreferences);
router.get("/user/:userId", getPreferencesByUserId);

export default router;