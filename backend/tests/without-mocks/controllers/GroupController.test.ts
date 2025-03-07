import request from 'supertest';
import { AppDataSource } from '../../../src/data-source';
import { Group } from '../../../src/entity/Group';
import { Game } from '../../../src/entity/Game';
import { User } from '../../../src/entity/User';

const BASE_URL = 'http://localhost:3000';

// Create test data before each test
beforeAll(async () => {
  // Create a test user
  await request(BASE_URL).post('/users').send({
    discord_id: `test_user_${Date.now()}`,
    username: `test_user_${Date.now()}`,
    email: `test_user_${Date.now()}@example.com`,
  });

  // Create a test game
  await request(BASE_URL).post('/games').send({
    game_name: `test_game_${Date.now()}`,
    description: 'A test game',
  });

  // Fetch the created game to get its ID
  const gameResponse = await request(BASE_URL).get('/games');
  const gameId = gameResponse.body[0].game_id;

  // Create test group
  await request(BASE_URL).post('/groups').send({
    game_id: gameId,
    group_name: `test_group_${Date.now()}`,
    max_players: 5,
  });
});

// Group: GroupRoutes - No Mocks
describe('GroupRoutes - No Mocks', () => {
  // Input: Valid request to get all groups
  // Expected status code: 200
  // Expected behavior: Returns a list of groups
  // Expected output: Array of groups
  test('Get All Groups', async () => {
    const res = await request(BASE_URL).get('/groups');
    expect(res.status).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
  });

  // Input: Valid request to get group by ID
  // Expected status code: 200
  // Expected behavior: Returns the group with the specified ID
  // Expected output: Group object
  test('Get Group by ID', async () => {
    const groupsResponse = await request(BASE_URL).get('/groups');
    const groupId = groupsResponse.body[0].group_id;

    const res = await request(BASE_URL).get(`/groups/${groupId}`);
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('group_id', groupId);
  });

  // Input: Valid request to create a group
  // Expected status code: 201
  // Expected behavior: Creates a new group
  // Expected output: Group object
  test('Create Group', async () => {
    const gamesResponse = await request(BASE_URL).get('/games');
    const gameId = gamesResponse.body[0].game_id;

    const res = await request(BASE_URL).post('/groups').send({
      game_id: gameId,
      group_name: `test_group_${Date.now()}`,
      max_players: 5,
    });
    expect(res.status).toBe(201);
    expect(res.body).toHaveProperty('group_id');
  });

  // Input: Valid request to update a group
  // Expected status code: 200
  // Expected behavior: Updates the group
  // Expected output: Updated group object
  test('Update Group', async () => {
    const groupsResponse = await request(BASE_URL).get('/groups');
    const groupId = groupsResponse.body[0].group_id;

    const res = await request(BASE_URL).put(`/groups/${groupId}`).send({
      group_name: `updated_group_${Date.now()}`,
    });
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('group_name');
  });

  // Input: Valid request to delete a group
  // Expected status code: 204
  // Expected behavior: Deletes the group
  // Expected output: No content
  test('Delete Group', async () => {
    // Step 1: Create a new game
    const createGameResponse = await request(BASE_URL).post('/games').send({
      game_name: `test_game_${Date.now()}`,
      description: 'A test game for group deletion',
    });
  
    expect(createGameResponse.status).toBe(201);
    const gameId = createGameResponse.body.game_id;
  
    // Step 2: Create a new group
    const createGroupResponse = await request(BASE_URL).post('/groups').send({
      game_id: gameId,
      group_name: `test_group_${Date.now()}`,
      max_players: 5,
    });
  
    expect(createGroupResponse.status).toBe(201);
    const groupId = createGroupResponse.body.group_id;
  
    // Step 3: Delete the group
    const deleteResponse = await request(BASE_URL).delete(`/groups/${groupId}`);
    expect(deleteResponse.status).toBe(204);
  
    // Step 4: Verify the group is deleted
    const getResponse = await request(BASE_URL).get(`/groups/${groupId}`);
    expect(getResponse.status).toBe(404);
    expect(getResponse.body).toHaveProperty('message', 'Group not found');
  });
  
  // Input: Valid request to join a group
  // Expected status code: 200
  // Expected behavior: Joins the group
  // Expected output: Updated group object
  test('Join Group', async () => {
    const groupsResponse = await request(BASE_URL).get('/groups');
    const groupId = groupsResponse.body[0].group_id;

    const usersResponse = await request(BASE_URL).get('/users');
    const discordId = usersResponse.body[0].discord_id;

    // Ensure the user and group exist
    expect(groupId).toBeDefined();
    expect(discordId).toBeDefined();

    // Join the group
    const res = await request(BASE_URL).post(`/groups/${groupId}/join`).send({
      discord_id: discordId,
    });

    // Check the response
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('members');
  });

  // Input: Valid request to leave a group
  // Expected status code: 200
  // Expected behavior: Leaves the group
  // Expected output: Updated group object
  test('Leave Group', async () => {
    const groupsResponse = await request(BASE_URL).get('/groups');
    const groupId = groupsResponse.body[0].group_id;

    const usersResponse = await request(BASE_URL).get('/users');
    const discordId = usersResponse.body[0].discord_id;

    // Ensure the user and group exist
    expect(groupId).toBeDefined();
    expect(discordId).toBeDefined();

    // First, join the group to ensure the user is a member
    await request(BASE_URL).post(`/groups/${groupId}/join`).send({
      discord_id: discordId,
    });

    // Then, leave the group
    const res = await request(BASE_URL).delete(`/groups/${groupId}/leave`).send({
      discord_id: discordId,
    });

    // Check the response
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('members');
  });

  // Input: Valid request to get group URL
  // Expected status code: 200
  // Expected behavior: Returns the group URL
  // Expected output: Group URL object
  test('Get Group URL', async () => {
    const groupsResponse = await request(BASE_URL).get('/groups');
    const groupId = groupsResponse.body[0].group_id;

    const res = await request(BASE_URL).get(`/groups/${groupId}/url`);
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('groupurl');
  });
});