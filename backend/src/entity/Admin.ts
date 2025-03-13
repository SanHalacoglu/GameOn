import { Entity, PrimaryGeneratedColumn, ManyToOne, JoinColumn, Column } from "typeorm";
import { User } from "./User";

@Entity()
export class Admin {
  @PrimaryGeneratedColumn()
  admin_id: number;

  @ManyToOne(() => User)
  @JoinColumn({ name: "discord_id" })
  user: User;
}