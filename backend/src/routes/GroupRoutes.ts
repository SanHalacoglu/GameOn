import express from "express";
import {
  getGroups,
  getGroupById,
  createGroup,
  updateGroup,
  deleteGroup,
  joinGroup,
  leaveGroup,
  getGroupMembers
  getGroupUrl,
} from "../controllers/GroupController";

const router = express.Router();

router.get("/", getGroups);
router.get("/:id", getGroupById);
router.post("/", createGroup);
router.put("/:id", updateGroup);
router.delete("/:id", deleteGroup);
router.post("/:id/join", joinGroup);
router.delete("/:id/leave", leaveGroup);
router.get("/:id/members", getGroupMembers)
router.get("/:id/url", getGroupUrl); 

export default router;