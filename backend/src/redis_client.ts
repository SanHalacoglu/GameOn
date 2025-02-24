import { createClient, RedisClientType } from "redis";

const redisUrl: string = process.env.REDIS_URL || "";

export const redisClient = createClient({ url: redisUrl });

export async function connectToRedisClient() {
    try {
        await redisClient.connect();
        console.log('Connected to Redis');
    } catch (error) {
        console.error('Failed to connect to Redis:', error);
        process.exit(1);
    }
}