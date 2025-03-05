import { Entity, PrimaryColumn, Column, OneToOne, JoinColumn } from "typeorm";
import { Preferences } from "./Preference";

@Entity()
export class User {

  @PrimaryColumn({ unique: true })
  discord_id!: string;

  @Column()
  username!: string;

  @Column({ unique: true })
  email!: string;

  @Column({ type: "timestamp", default: () => "CURRENT_TIMESTAMP" })
  created_at!: Date;

  @Column({ default: false })
  banned!: boolean;

  @Column({ default: null })
  avatar?: string;

  @OneToOne(() => Preferences, (preferences) => preferences.user)
  preferences!: Preferences;
}