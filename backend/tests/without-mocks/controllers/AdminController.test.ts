import request from 'supertest';
import { AppDataSource } from '../../../src/data-source';
import { User } from '../../../src/entity/User';
import { Admin } from '../../../src/entity/Admin';

const BASE_URL = 'http://localhost:3000';

// Group: AdminRoutes - No Mocks
describe('AdminRoutes - No Mocks', () => {
  // Input: Valid request to get all admins
  // Expected status code: 200
  // Expected behavior: Returns a list of admins
  // Expected output: Array of admins
  test('Get All Admins', async () => {
    const res = await request(BASE_URL).get('/admins');
    expect(res.status).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
  });

  // Input: Valid request to get admin by ID
  // Expected status code: 200
  // Expected behavior: Returns the admin with the specified ID
  // Expected output: Admin object
  test('Get Admin by ID', async () => {
    // Step 1: Create a user
    const userResponse = await request(BASE_URL).post('/users').send({
      discord_id: `test_user_${Date.now()}`,
      username: `test_user_${Date.now()}`,
      email: `test_user_${Date.now()}@example.com`,
    });
    const userId = userResponse.body.discord_id;

    // Step 2: Create an admin for the user
    const adminResponse = await request(BASE_URL).post('/admins').send({
      discord_id: userId,
      permissions: ['read', 'write'],
    });
    const adminId = adminResponse.body.admin_id;

    // Step 3: Fetch the admin by ID
    const res = await request(BASE_URL).get(`/admins/${adminId}`);
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('admin_id', adminId);
  });

  // Input: Valid request to create an admin
  // Expected status code: 201
  // Expected behavior: Creates a new admin
  // Expected output: Admin object
  test('Create Admin', async () => {
    // Step 1: Create a user
    const userResponse = await request(BASE_URL).post('/users').send({
      discord_id: `test_user_${Date.now()}`,
      username: `test_user_${Date.now()}`,
      email: `test_user_${Date.now()}@example.com`,
    });
    const userId = userResponse.body.discord_id;
  
    // Step 2: Create an admin for the user
    const res = await request(BASE_URL).post('/admins').send({
      discord_id: userId,
    });
    expect(res.status).toBe(201);
    expect(res.body).toHaveProperty('admin_id');
  });

  // Input: Valid request to update an admin
  // Expected status code: 200
  // Expected behavior: Updates the admin
  // Expected output: Updated admin object
  test('Update Admin', async () => {
  // Step 1: Create a user
  const userResponse = await request(BASE_URL).post('/users').send({
    discord_id: `test_user_${Date.now()}`,
    username: `test_user_${Date.now()}`,
    email: `test_user_${Date.now()}@example.com`,
  });
  const userId = userResponse.body.discord_id;

  // Step 2: Create an admin for the user
  const adminResponse = await request(BASE_URL).post('/admins').send({
    discord_id: userId,
  });
  const adminId = adminResponse.body.admin_id;

  // Step 3: Update the admin (no permissions field)
  const res = await request(BASE_URL).put(`/admins/${adminId}`).send({
    // Add other fields if needed
  });
  expect(res.status).toBe(200);
});

  // Input: Valid request to delete an admin
  // Expected status code: 204
  // Expected behavior: Deletes the admin
  // Expected output: No content
  test('Delete Admin', async () => {
    // Step 1: Create a user
    const userResponse = await request(BASE_URL).post('/users').send({
      discord_id: `test_user_${Date.now()}`,
      username: `test_user_${Date.now()}`,
      email: `test_user_${Date.now()}@example.com`,
    });
    const userId = userResponse.body.discord_id;

    // Step 2: Create an admin for the user
    const adminResponse = await request(BASE_URL).post('/admins').send({
      discord_id: userId,
      permissions: ['read'],
    });
    const adminId = adminResponse.body.admin_id;

    // Step 3: Delete the admin
    const deleteResponse = await request(BASE_URL).delete(`/admins/${adminId}`);
    expect(deleteResponse.status).toBe(204);

    // Step 4: Verify the admin is deleted
    const getResponse = await request(BASE_URL).get(`/admins/${adminId}`);
    expect(getResponse.status).toBe(404);
    expect(getResponse.body).toHaveProperty('message', 'Admin not found');
  });
});