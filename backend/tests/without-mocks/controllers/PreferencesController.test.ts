import request from 'supertest';

const port = process.env.PORT; 
const BASE_URL = `http://localhost:${port}`;

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
  // Expected behavior: Returns all preferences stored in the database
  // Expected output: Array of preferences
  test('Get All Preferences', async () => {
    const res = await request(BASE_URL).get('/preferences');
    expect(res.status).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
  });

  // Input: Valid request to get preferences by ID
  // Expected status code: 200
  // Expected behavior: Returns the preference with the specified ID
  // Expected output: Preference object with matching preference_id
  test('Get Preferences by ID', async () => {
    const gamesResponse = await request(BASE_URL).get('/games');
    const gameId = gamesResponse.body[0].game_id;

    const createResponse = await request(BASE_URL).post('/preferences').send({
      spoken_language: 'English',
      time_zone: 'PST',
      skill_level: 'casual',
      discord_id: 'test_user_id',
      game_id: gameId,
    });

    expect(createResponse.status).toBe(201);
    const preferenceId = createResponse.body.preference_id;

    const getResponse = await request(BASE_URL).get(`/preferences/${preferenceId}`);
    expect(getResponse.status).toBe(200);
    expect(getResponse.body).toHaveProperty('preference_id', preferenceId);
  });

  // Input: Valid request to get preferences by user ID
  // Expected status code: 200
  // Expected behavior: Returns the preferences associated with the specified user ID
  // Expected output: Object containing user details and their preferences
  test('Get Preferences by User ID', async () => {
    const res = await request(BASE_URL).get('/preferences/user/test_user_id');
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('user');
    expect(res.body.user).toHaveProperty('discord_id', 'test_user_id');
  });

  // Input: Valid request to create preferences
  // Expected status code: 201
  // Expected behavior: Creates a new preference entry in the database
  // Expected output: Object containing the newly created preference_id
  test('Create Preferences', async () => {
    const gamesResponse = await request(BASE_URL).get('/games');
    const gameId = gamesResponse.body[0].game_id;

    const res = await request(BASE_URL).post('/preferences').send({
      spoken_language: 'English',
      time_zone: 'PST',
      skill_level: 'casual',
      discord_id: 'test_user_id',
      game_id: gameId,
    });
    expect(res.status).toBe(201);
    expect(res.body).toHaveProperty('preference_id');
  });

  // Input: Valid request to update preferences
  // Expected status code: 200
  // Expected behavior: Updates the specified preference with new data
  // Expected output: Updated preference object with the new values
  test('Update Preferences', async () => {
    const gamesResponse = await request(BASE_URL).get('/games');
    const gameId = gamesResponse.body[0].game_id;

    const createResponse = await request(BASE_URL).post('/preferences').send({
      spoken_language: 'English',
      time_zone: 'PST',
      skill_level: 'casual',
      discord_id: 'test_user_id',
      game_id: gameId,
    });

    expect(createResponse.status).toBe(201);
    const preferenceId = createResponse.body.preference_id;

    const updateResponse = await request(BASE_URL).put(`/preferences/${preferenceId}`).send({
      spoken_language: 'Spanish',
    });

    expect(updateResponse.status).toBe(200);
    expect(updateResponse.body).toHaveProperty('spoken_language', 'Spanish');
  });
});