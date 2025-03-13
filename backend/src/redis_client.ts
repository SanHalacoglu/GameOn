import { createClient, RedisClientType } from "redis";

const redisUrl = process.env.REDIS_URL ?? "redis://redis:6379";

if (!redisUrl) {
  throw new Error("REDIS_URL environment variable is not defined!");
}

export const redisClient = createClient({ url: redisUrl });

export async function connectToRedisClient() {
    try {
        await redisClient.connect();
        console.log('Connected to Redis');
    } catch (error: unknown) {
        console.error('Failed to connect to Redis:', error);
        process.exit(1);
    }
}