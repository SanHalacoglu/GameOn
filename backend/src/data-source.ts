import { DataSource } from "typeorm";
import { User } from "./entity/User";
import { Admin } from "./entity/Admin";
import { Game } from "./entity/Game";
import { Group } from "./entity/Group";
import { GroupMember } from "./entity/GroupMember";
import { Preferences } from "./entity/Preference";
import { Report } from "./entity/Report";
import * as dotenv from "dotenv";

// Load environment variables from .env file
dotenv.config();

export const AppDataSource = new DataSource({
  type: "mysql",
  host: process.env.MYSQL_HOST,
  port: parseInt(process.env.MYSQL_PORT_INTER || "3306"),
  username: process.env.MYSQL_USER,
  password: process.env.MYSQL_PASSWORD,
  database: process.env.MYSQL_DB,
  synchronize: true,
  logging: true,
  entities: [User, Admin, Game, Group, Report, GroupMember, Preferences],
  migrations: [],
  subscribers: [],
});