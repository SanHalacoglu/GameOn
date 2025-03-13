import request from 'supertest';

const port = process.env.PORT; 
const BASE_URL = `http://localhost:${port}`;

// Create test data before all tests
beforeAll(async () => {
  // Create a test game
  await request(BASE_URL).post('/games').send({
    game_name: 'Test Game',
    description: 'A test game for integration testing',
  });
});

// Group: GamesRoutes - No Mocks
describe('GamesRoutes - No Mocks', () => {
  // Input: Valid request to get all games
  // Expected status code: 200
  // Expected behavior: Returns a list of games
  // Expected output: Array of games
  test('Get All Games', async () => {
    const res = await request(BASE_URL).get('/games');
    expect(res.status).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
    expect(res.body.length).toBeGreaterThan(0);
  });

  // Input: Valid request to get a game by ID
  // Expected status code: 200
  // Expected behavior: Returns the game with the specified ID
  // Expected output: Game object
  test('Get Game by ID', async () => {
    // Fetch the created game to get its ID
    const gamesResponse = await request(BASE_URL).get('/games');
    const gameId = gamesResponse.body[0].game_id;

    const res = await request(BASE_URL).get(`/games/${gameId}`);
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('game_id', gameId);
  });

  // Input: Request to get a game with an invalid ID
  // Expected status code: 404
  // Expected behavior: Returns a "Game not found" message
  // Expected output: { message: "Game not found" }
  test('Get Game by Invalid ID', async () => {
    const res = await request(BASE_URL).get('/games/9999');
    expect(res.status).toBe(404);
    expect(res.body).toHaveProperty('message', 'Game not found');
  });

  // Input: Valid request to create a new game
  // Expected status code: 201
  // Expected behavior: Creates a new game
  // Expected output: Game object
  test('Create Game', async () => {
    const uniqueGameName = `New Game ${Date.now()}`; // Ensure the name is unique
    const res = await request(BASE_URL).post('/games').send({
      game_name: uniqueGameName,
      description: 'A brand new game',
    });
    expect(res.status).toBe(201);
    expect(res.body).toHaveProperty('game_id');
    expect(res.body).toHaveProperty('game_name', uniqueGameName);
  });

  // Input: Request to create a game with an existing name
  // Expected status code: 409
  // Expected behavior: Returns a "Game with this name already exists" message
  // Expected output: { message: "Game with this name already exists" }
  test('Create Game with Existing Name', async () => {
    const res = await request(BASE_URL).post('/games').send({
      game_name: 'Test Game', // This name was created in beforeAll
      description: 'A duplicate game',
    });
    expect(res.status).toBe(409);
    expect(res.body).toHaveProperty('message', 'Game with this name already exists');
  });

  // Input: Valid request to update a game
  // Expected status code: 200
  // Expected behavior: Updates the game with the specified ID
  // Expected output: Updated game object
  test('Update Game', async () => {
    // Fetch the created game to get its ID and current data
    const gamesResponse = await request(BASE_URL).get('/games');
    const game = gamesResponse.body[0];
    const gameId = game.game_id;

    // Update the game data
    const updatedGameData = {
      ...game,
      description: 'Updated description',
    };

    const res = await request(BASE_URL).put(`/games/${gameId}`).send(updatedGameData);
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('description', 'Updated description');
  });

  // Input: Request to update a game with an invalid ID
  // Expected status code: 404
  // Expected behavior: Returns a "Game not found" message
  // Expected output: { message: "Game not found" }
  test('Update Game with Invalid ID', async () => {
    const res = await request(BASE_URL).put('/games/9999').send({
      description: 'This should not work',
    });
    expect(res.status).toBe(404);
    expect(res.body).toHaveProperty('message', 'Game not found');
  });

  // Input: Valid request to delete a game
  // Expected status code: 204
  // Expected behavior: Deletes the game with the specified ID
  // Expected output: No content
  test('Delete Game', async () => {
    // Step 1: Create a new game
    const uniqueGameName = `New Game ${Date.now()}`; // Ensure the name is unique
    const createResponse = await request(BASE_URL).post('/games').send({
      game_name: uniqueGameName,
      description: 'A game to be deleted',
    });
  
    expect(createResponse.status).toBe(201);
    const gameId = createResponse.body.game_id;
  
    // Step 2: Delete any preferences referencing this game (if applicable)
    const preferencesResponse = await request(BASE_URL).get('/preferences');
    const preferencesToDelete = preferencesResponse.body.filter(
      (pref: any) => pref.game.game_id === gameId
    );
  
    for (const pref of preferencesToDelete) {
      await request(BASE_URL).delete(`/preferences/${pref.preference_id}`);
    }
  
    // Step 3: Delete the game
    const deleteResponse = await request(BASE_URL).delete(`/games/${gameId}`);
    expect(deleteResponse.status).toBe(204);
  
    // Step 4: Verify the game is deleted
    const getResponse = await request(BASE_URL).get(`/games/${gameId}`);
    expect(getResponse.status).toBe(404);
    expect(getResponse.body).toHaveProperty('message', 'Game not found');
  });

  // Input: Request to delete a game with an invalid ID
  // Expected status code: 404
  // Expected behavior: Returns a "Game not found" message
  // Expected output: { message: "Game not found" }
  test('Delete Game with Invalid ID', async () => {
    const res = await request(BASE_URL).delete('/games/9999');
    expect(res.status).toBe(404);
    expect(res.body).toHaveProperty('message', 'Game not found');
  });
});