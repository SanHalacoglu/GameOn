import request from 'supertest';
import { AppDataSource } from '../../../src/data-source';
import { User } from '../../../src/entity/User';

const port = process.env.PORT; 
const BASE_URL = `http://localhost:${port}`;

// Create test data before each test
beforeAll(async () => {
  // Create a test user
  await request(BASE_URL).post('/users').send({
    discord_id: `test_user_${Date.now()}`,
    username: `test_user_${Date.now()}`,
    email: `test_user_${Date.now()}@example.com`,
  });
});

// Group: UserRoutes - No Mocks
describe('UserRoutes - No Mocks', () => {
  // Input: Valid request to get all users
  // Expected status code: 200
  // Expected behavior: Returns a list of users
  // Expected output: Array of users
  test('Get All Users', async () => {
    const res = await request(BASE_URL).get('/users');
    expect(res.status).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
  });

  // Input: Valid request to get user by ID
  // Expected status code: 200
  // Expected behavior: Returns the user with the specified ID
  // Expected output: User object
  test('Get User by ID', async () => {
    const usersResponse = await request(BASE_URL).get('/users');
    const userId = usersResponse.body[0].discord_id;

    const res = await request(BASE_URL).get(`/users/${userId}`);
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('discord_id', userId);
  });

  // Input: Valid request to create a user
  // Expected status code: 201
  // Expected behavior: Creates a new user
  // Expected output: User object
  test('Create User', async () => {
    const res = await request(BASE_URL).post('/users').send({
      discord_id: `test_user_${Date.now()}`,
      username: `test_user_${Date.now()}`,
      email: `test_user_${Date.now()}@example.com`,
    });
    expect(res.status).toBe(201);
    expect(res.body).toHaveProperty('discord_id');
  });

  // Input: Valid request to update a user
  // Expected status code: 200
  // Expected behavior: Updates the user
  // Expected output: Updated user object
  test('Update User', async () => {
    const usersResponse = await request(BASE_URL).get('/users');
    const userId = usersResponse.body[0].discord_id;

    const res = await request(BASE_URL).put(`/users/${userId}`).send({
      username: `updated_user_${Date.now()}`,
    });
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('username');
  });

  // Input: Valid request to delete a user
  // Expected status code: 204
  // Expected behavior: Deletes the user
  // Expected output: No content
  test('Delete User', async () => {
    // Step 1: Create a new user
    const uniqueDiscordId = `test_user_${Date.now()}`; // Ensure the ID is unique
    const createResponse = await request(BASE_URL).post('/users').send({
      discord_id: uniqueDiscordId,
      username: `test_user_${Date.now()}`,
      email: `test_user_${Date.now()}@example.com`,
    });
  
    expect(createResponse.status).toBe(201);
    const userId = createResponse.body.discord_id;
  
    // Step 2: Delete the user
    const deleteResponse = await request(BASE_URL).delete(`/users/${userId}`);
    expect(deleteResponse.status).toBe(204);
  
    // Step 3: Verify the user is deleted
    const getResponse = await request(BASE_URL).get(`/users/${userId}`);
    expect(getResponse.status).toBe(404);
    expect(getResponse.body).toHaveProperty('message', 'User not found');
  });
  
  // Input: Valid request to get user groups
  // Expected status code: 200
  // Expected behavior: Returns the groups the user is a member of
  // Expected output: Array of groups
  test('Get User Groups', async () => {
    // Step 1: Create a new user
    const createUserResponse = await request(BASE_URL).post('/users').send({
      discord_id: `test_user_${Date.now()}`,
      username: `test_user_${Date.now()}`,
      email: `test_user_${Date.now()}@example.com`,
    });
  
    const userId = createUserResponse.body.discord_id;
    console.log(`Created user with ID: ${userId}`);
  
    // Step 2: Create a new game (required for creating a group)
    const createGameResponse = await request(BASE_URL).post('/games').send({
      game_name: `test_game_${Date.now()}`,
      description: 'A test game',
    });
  
    const gameId = createGameResponse.body.game_id;
    console.log(`Created game with ID: ${gameId}`);
  
    // Step 3: Create a new group
    const createGroupResponse = await request(BASE_URL).post('/groups').send({
      game_id: gameId,
      group_name: `test_group_${Date.now()}`,
      max_players: 5,
    });
  
    const groupId = createGroupResponse.body.group_id;
    console.log(`Created group with ID: ${groupId}`);
  
    // Step 4: Add the user to the group
    await request(BASE_URL).post(`/groups/${groupId}/join`).send({
      discord_id: userId,
    });
  
    // Step 5: Fetch the groups for the newly created user
    const res = await request(BASE_URL).get(`/users/${userId}/groups`);
    expect(res.status).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
    expect(res.body.length).toBeGreaterThan(0); // Ensure the user is in at least one group
  });
  // Input: Valid request to ban a user
  // Expected status code: 200
  // Expected behavior: Bans the user
  // Expected output: User object with banned status set to true
  test('Ban User', async () => {
    const usersResponse = await request(BASE_URL).get('/users');
    const userId = usersResponse.body[0].discord_id;

    const res = await request(BASE_URL).put(`/users/${userId}/ban`);
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('banned', true);
  });
});