import request from 'supertest';

const BASE_URL = 'http://localhost:3000'; // Adjust the port if necessary

// Create test data before each test
beforeAll(async () => {
  // Create a test user
  await request(BASE_URL).post('/users').send({
    discord_id: 'test_user_id',
    username: 'test_user',
    email: 'test_user@example.com',
  });

  // Create a test game
  await request(BASE_URL).post('/games').send({
    game_name: 'test_game',
    description: 'A test game',
  });

  // Fetch the created game to get its ID
  const gameResponse = await request(BASE_URL).get('/games');
  const gameId = gameResponse.body[0].game_id;

  // Create test preferences
  await request(BASE_URL).post('/preferences').send({
    spoken_language: 'English',
    time_zone: 'PST',
    skill_level: 'casual',
    discord_id: 'test_user_id',
    game_id: gameId,
  });
});

// Group: PreferencesRoutes - No Mocks
describe('PreferencesRoutes - No Mocks', () => {
  // Input: Valid request to get all preferences
  // Expected status code: 200
  // Expected behavior: Returns a list of preferences
  // Expected output: Array of preferences
  test('Get All Preferences', async () => {
    const res = await request(BASE_URL).get('/preferences');
    expect(res.status).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
  });

  // Input: Valid request to get preferences by ID
  // Expected status code: 200
  // Expected behavior: Returns the preferences with the specified ID
  // Expected output: Preferences object
  test('Get Preferences by ID', async () => {
    const res = await request(BASE_URL).get('/preferences/1');
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('preference_id', 1);
  });

  // Input: Valid request to get preferences by user ID
  // Expected status code: 200
  // Expected behavior: Returns the preferences for the specified user
  // Expected output: Preferences object
  test('Get Preferences by User ID', async () => {
    const res = await request(BASE_URL).get('/preferences/user/test_user_id');
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('user');
    expect(res.body.user).toHaveProperty('discord_id', 'test_user_id');
  });

  // Input: Valid request to create preferences
  // Expected status code: 201
  // Expected behavior: Creates new preferences
  // Expected output: Preferences object
  test('Create Preferences', async () => {
    const res = await request(BASE_URL).post('/preferences').send({
      spoken_language: 'English',
      time_zone: 'PST',
      skill_level: 'casual',
      discord_id: 'test_user_id',
      game_id: 1,
    });
    expect(res.status).toBe(201);
    expect(res.body).toHaveProperty('preference_id');
  });

  // Input: Valid request to update preferences
  // Expected status code: 200
  // Expected behavior: Updates the preferences
  // Expected output: Updated preferences object
  test('Update Preferences', async () => {
    const res = await request(BASE_URL).put('/preferences/1').send({
      spoken_language: 'Spanish',
    });
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('spoken_language', 'Spanish');
  });
});