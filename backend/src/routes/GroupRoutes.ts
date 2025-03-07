import express from "express";
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
} from "../controllers/GroupController";

const router = express.Router();

router.get("/", getGroups);
router.get("/:group_id", getGroupById);
router.post("/", createGroup);
router.put("/:group_id", updateGroup);
router.delete("/:group_id", deleteGroup);
router.post("/:group_id/join", joinGroup);
router.delete("/:group_id/leave", leaveGroup);
router.get("/:group_id/members", getGroupMembers)
router.get("/:group_id/url", getGroupUrl); 

export default router;