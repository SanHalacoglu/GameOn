import request from 'supertest';

const port = process.env.PORT; 
const BASE_URL = `http://localhost:${port}`;

describe('AdminRoutes - No Mocks', () => {

    //Non-Functional Reqs - Security. More Endpoints Are Secure. This is for demonstration purposes only.
  test('Ensure Auth Endpoints Are Secure. No Session Information. Register.', async () => {
    console.log('BASE_URL:', BASE_URL);
    const res = await request(BASE_URL).post('/auth/register');
    expect(res.status).toBe(401);
    expect(res.body.message).toBeDefined();
  });

  test('Ensure Auth Endpoints Are Secure. No Session Information. Logout.', async () => {
    const res = await request(BASE_URL).post('/auth/logout');
    expect(res.status).toBe(401);
    expect(res.body.message).toBeDefined();
  });
});