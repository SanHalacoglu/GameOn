import { Request, Response } from "express";
import { AppDataSource } from "../data-source";
import { Admin } from "../entity/Admin";
import { User } from "../entity/User";

export const getAdmins = async (req: Request, res: Response): Promise<void> => {
  try {
    const adminRepository = AppDataSource.getRepository(Admin);
    const admins = await adminRepository.find({ relations: ["user"] });
    res.json(admins);
  } catch (error) {
    console.error("Error fetching admins:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const getAdminById = async (req: Request, res: Response): Promise<void> => {
  try {
    const adminRepository = AppDataSource.getRepository(Admin);
    const admin = await adminRepository.findOne({
      where: { admin_id: parseInt(req.params.id) },
      relations: ["user"],
    });
    if (admin) {
      res.json(admin);
    } else {
      res.status(404).json({ message: "Admin not found" });
    }
  } catch (error) {
    console.error("Error fetching admin by ID:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const createAdmin = async (req: Request, res: Response): Promise<void> => {
  const { discord_id } = req.body;

  if (!discord_id) {
    res.status(400).json({ message: "discord_id  are required" });
    return;
  }

  const userRepository = AppDataSource.getRepository(User);
  const adminRepository = AppDataSource.getRepository(Admin);

  try {
    const user = await userRepository.findOne({
      where: { discord_id: discord_id },
    });

    if (!user) {
      res.status(404).json({ message: "User not found" });
      return;
    }

    const admin = adminRepository.create({
      user,
    });

    await adminRepository.save(admin);
    res.status(201).json(admin);
  } catch (error) {
    console.error("Error creating admin:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const updateAdmin = async (req: Request, res: Response): Promise<void> => {
  try {
    const adminRepository = AppDataSource.getRepository(Admin);
    const admin = await adminRepository.findOne({
      where: { admin_id: parseInt(req.params.id) },
    });
    if (admin) {
      const { ...updateData } = req.body; // Exclude admin_id from the request body
      adminRepository.merge(admin, updateData);
      await adminRepository.save(admin);
      res.json(admin);
    } else {
      res.status(404).json({ message: "Admin not found" });
    }
  } catch (error) {
    console.error("Error updating admin:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};

export const deleteAdmin = async (req: Request, res: Response): Promise<void> => {
  try {
    const adminRepository = AppDataSource.getRepository(Admin);
    const admin = await adminRepository.findOne({
      where: { admin_id: parseInt(req.params.id) },
    });
    if (admin) {
      await adminRepository.remove(admin);
      res.status(204).send();
    } else {
      res.status(404).json({ message: "Admin not found" });
    }
  } catch (error) {
    console.error("Error deleting admin:", error);
    res.status(500).json({ message: "Internal server error" });
  }
};