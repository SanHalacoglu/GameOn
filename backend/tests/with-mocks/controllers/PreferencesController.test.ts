import { getPreferencesById, createPreferences, updatePreferences, deletePreferences, getPreferencesByUserId } from '../../../src/controllers/PreferencesController';
import { Request, Response } from 'express';

jest.mock('../../../src/controllers/PreferencesController');

// Group: PreferencesRoutes - With Mocks
describe('PreferencesRoutes - With Mocks', () => {
  let req: Partial<Request>;
  let res: Partial<Response>;

  beforeEach(() => {
    req = {};
    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn(),
      send: jest.fn(),
    };
  });

  // Mocked behavior: getPreferencesById throws an error
  // Input: Valid request to get preferences by ID
  // Expected status code: 500
  // Expected behavior: Error is handled gracefully
  // Expected output: Error message
  test('Get Preferences by ID - Error', async () => {
    (getPreferencesById as jest.Mock).mockImplementation(() => {
      throw new Error('Forced error');
    });

    req.params = { id: '1' };
    await getPreferencesById(req as Request, res as Response);
    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.send.mock.calls[0][0]).toBe('Internal Server Error');
  });

  // Mocked behavior: getPreferencesById returns valid preferences
  // Input: Valid request to get preferences by ID
  // Expected status code: 200
  // Expected behavior: Returns the preferences with the specified ID
  // Expected output: Preferences object
  test('Get Preferences by ID - Success', async () => {
    (getPreferencesById as jest.Mock).mockResolvedValue({
      preference_id: 1,
      spoken_language: 'English',
      time_zone: 'PST',
      skill_level: 'casual',
      user: { discord_id: 'test_user_id' },
      game: { game_id: 1 },
    });

    req.params = { id: '1' };
    await getPreferencesById(req as Request, res as Response);
    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json.mock.calls[0][0]).toHaveProperty('preference_id', 1);
  });

  // Mocked behavior: createPreferences throws an error
  // Input: Valid request to create preferences
  // Expected status code: 500
  // Expected behavior: Error is handled gracefully
  // Expected output: Error message
  test('Create Preferences - Error', async () => {
    (createPreferences as jest.Mock).mockImplementation(() => {
      throw new Error('Forced error');
    });

    req.body = {
      discord_id: 'test_user_id',
      spoken_language: 'English',
      time_zone: 'PST',
      skill_level: 'casual',
      game_id: 1,
    };
    await createPreferences(req as Request, res as Response);
    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.send.mock.calls[0][0]).toBe('Internal Server Error');
  });

  // Mocked behavior: createPreferences returns valid preferences
  // Input: Valid request to create preferences
  // Expected status code: 201
  // Expected behavior: Creates new preferences
  // Expected output: Preferences object
  test('Create Preferences - Success', async () => {
    (createPreferences as jest.Mock).mockResolvedValue({
      preference_id: 1,
      spoken_language: 'English',
      time_zone: 'PST',
      skill_level: 'casual',
      user: { discord_id: 'test_user_id' },
      game: { game_id: 1 },
    });

    req.body = {
      discord_id: 'test_user_id',
      spoken_language: 'English',
      time_zone: 'PST',
      skill_level: 'casual',
      game_id: 1,
    };
    await createPreferences(req as Request, res as Response);
    expect(res.status).toHaveBeenCalledWith(201);
    expect(res.json.mock.calls[0][0]).toHaveProperty('preference_id', 1);
  });

  // Mocked behavior: updatePreferences throws an error
  // Input: Valid request to update preferences
  // Expected status code: 500
  // Expected behavior: Error is handled gracefully
  // Expected output: Error message
  test('Update Preferences - Error', async () => {
    (updatePreferences as jest.Mock).mockImplementation(() => {
      throw new Error('Forced error');
    });

    req.params = { id: '1' };
    req.body = { spoken_language: 'Spanish' };
    await updatePreferences(req as Request, res as Response);
    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.send.mock.calls[0][0]).toBe('Internal Server Error');
  });

  // Mocked behavior: updatePreferences returns updated preferences
  // Input: Valid request to update preferences
  // Expected status code: 200
  // Expected behavior: Updates the preferences
  // Expected output: Updated preferences object
  test('Update Preferences - Success', async () => {
    (updatePreferences as jest.Mock).mockResolvedValue({
      preference_id: 1,
      spoken_language: 'Spanish',
      time_zone: 'PST',
      skill_level: 'casual',
      user: { discord_id: 'test_user_id' },
      game: { game_id: 1 },
    });

    req.params = { id: '1' };
    req.body = { spoken_language: 'Spanish' };
    await updatePreferences(req as Request, res as Response);
    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json.mock.calls[0][0]).toHaveProperty('spoken_language', 'Spanish');
  });

  // Mocked behavior: deletePreferences throws an error
  // Input: Valid request to delete preferences
  // Expected status code: 500
  // Expected behavior: Error is handled gracefully
  // Expected output: Error message
  test('Delete Preferences - Error', async () => {
    (deletePreferences as jest.Mock).mockImplementation(() => {
      throw new Error('Forced error');
    });

    req.params = { id: '1' };
    await deletePreferences(req as Request, res as Response);
    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.send.mock.calls[0][0]).toBe('Internal Server Error');
  });

  // Mocked behavior: deletePreferences succeeds
  // Input: Valid request to delete preferences
  // Expected status code: 204
  // Expected behavior: Deletes the preferences
  // Expected output: None
  test('Delete Preferences - Success', async () => {
    (deletePreferences as jest.Mock).mockResolvedValue(undefined);

    req.params = { id: '1' };
    await deletePreferences(req as Request, res as Response);
    expect(res.status).toHaveBeenCalledWith(204);
  });

  // Mocked behavior: getPreferencesByUserId throws an error
  // Input: Valid request to get preferences by user ID
  // Expected status code: 500
  // Expected behavior: Error is handled gracefully
  // Expected output: Error message
  test('Get Preferences by User ID - Error', async () => {
    (getPreferencesByUserId as jest.Mock).mockImplementation(() => {
      throw new Error('Forced error');
    });

    req.params = { userId: 'test_user_id' };
    await getPreferencesByUserId(req as Request, res as Response);
    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.send.mock.calls[0][0]).toBe('Internal Server Error');
  });

  // Mocked behavior: getPreferencesByUserId returns valid preferences
  // Input: Valid request to get preferences by user ID
  // Expected status code: 200
  // Expected behavior: Returns the preferences for the specified user
  // Expected output: Preferences object
  test('Get Preferences by User ID - Success', async () => {
    (getPreferencesByUserId as jest.Mock).mockResolvedValue({
      preference_id: 1,
      spoken_language: 'English',
      time_zone: 'PST',
      skill_level: 'casual',
      user: { discord_id: 'test_user_id' },
      game: { game_id: 1 },
    });

    req.params = { userId: 'test_user_id' };
    await getPreferencesByUserId(req as Request, res as Response);
    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json.mock.calls[0][0]).toHaveProperty('user');
    expect(res.json.mock.calls[0][0].user).toHaveProperty('discord_id', 'test_user_id');
  });
});