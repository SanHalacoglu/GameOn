import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { Report } from "../entity/Report";
import { User } from "../entity/User";
import { Group } from "../entity/Group";
import axios from "axios";

const DB_SERVICE_URL = process.env.DB_SERVICE_URL || "";

export const getReports = async (req: Request, res: Response) => {
  try {
    const reportRepository = AppDataSource.getRepository(Report);
    const reports = await reportRepository.find({ 
      relations: ["reporter", "reported_user", "group"],
      where: req.query.unresolved == "true" ? { resolved: false } : undefined
    });
    res.json(reports);
  } catch (error) {
    console.error("Error fetching reports:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const getReportById = async (req: Request, res: Response) => {
  try {
    const reportRepository = AppDataSource.getRepository(Report);
    const report = await reportRepository.findOne({
      where: { report_id: parseInt(req.params.id) },
      relations: ["reporter", "reported_user", "group"],
    });
    if (report) {
      res.json(report);
    } else {
      res.status(404).json({ message: "Report not found" });
    }
  } catch (error) {
    console.error("Error fetching report by ID:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const createReport = async (req: Request, res: Response) => {
  const reporter_discord_id = req.session.user!.discord_id;
  const { reported_discord_id, group_id, reason } = req.body;

  if (!reporter_discord_id || !reported_discord_id || !group_id || !reason) {
    res.status(400).json({ message: "All fields are required" });
    return;
  }

  const reportRepository = AppDataSource.getRepository(Report);
  const userRepository = AppDataSource.getRepository(User);
  const groupRepository = AppDataSource.getRepository(Group);

  try {
    const reporter = await userRepository.findOne({
      where: { discord_id: reporter_discord_id }
    });
    if (!reporter) {
      res.status(404).json({ message: "Reporter not found" });
      return;
    }

    const reported_user = await userRepository.findOne({
      where: { discord_id: reported_discord_id }
    });
    if (!reported_user) {
      res.status(404).json({ message: "Reported user not found" });
      return;
    }

    const group = await groupRepository.findOne({
      where: { group_id: group_id }
    });
    if (!group) {
      res.status(404).json({ message: "Group not found" });
      return;
    }

    const report = reportRepository.create({
      reporter,
      reported_user,
      group,
      reason
    });
    await reportRepository.save(report);
    res.status(201).json(report);

  } catch (error) {
    console.error("Error creating report:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const resolveReport = async (req: Request, res: Response) => {
  try {
    const reportRepository = AppDataSource.getRepository(Report);
    const report = await reportRepository.findOne({
      where: { report_id: parseInt(req.params.id) },
      relations: ["reported_user"]
    });
    if (report) {
      report.resolved = true;
      await reportRepository.save(report);

      if (req.query.ban == "true") {
        await axios.put(`${DB_SERVICE_URL}/users/${report.reported_user.discord_id}/ban`, {
          responseType: 'json'
        });
      }

      res.json(report);
    } else {
      res.status(404).json({ message: "Report not found" });
    }

  } catch (error) {
    console.error("Error resolving report:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const deleteReport = async (req: Request, res: Response) => {
  try {
    const reportRepository = AppDataSource.getRepository(Report);
    const report = await reportRepository.findOne({
      where: { report_id: parseInt(req.params.id) },
    });
    if (report) {
      await reportRepository.remove(report);
      res.status(204).send();
    } else {
      res.status(404).json({ message: "Report not found" });
    }
  } catch (error) {
    console.error("Error deleting report:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};