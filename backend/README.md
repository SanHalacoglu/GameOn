## Setup Instructions
1. **Install Dependencies:**
    ```
    npm install
    ```
2. **Environment Variables:**
   Create a .env file in the project root (backend folder) with the following variables:
    ```
    DISCORD_CLIENT_ID=your_discord_client_id
    DISCORD_CLIENT_SECRET=your_discord_client_secret
    DISCORD_REDIRECT_URI=http://localhost:3000/auth/callback_discord
    PORT=3000
    ```
3. **Running the Project:**
   ```
   npx ts-node src/index.ts
   ```

## Example Folder
There is an example_frontend folder that replicates a simple frontend. Open the html file in your browser and make sure the backend is running on the specified port.

## Database Setup and Running the Server

### Step 1: Set Up the Database

**Install MySQL:**

If you don't already have MySQL installed, download and install it from the official website.

Alternatively, you can use a Docker container for MySQL:
```bash
docker run --name gameon-db -e MYSQL_ROOT_PASSWORD=yourpassword -p 3306:3306 -d mysql
```

**Create a Database:**

Connect to your MySQL instance using a client like mysql or a GUI tool like MySQL Workbench.

Create a database for your project:
```sql
CREATE DATABASE gameon_db;
```

**Update Database Configuration:**

Create or update your `.env` file with the following database connection details:

```
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_USER=root
MYSQL_PASSWORD=yourpassword
MYSQL_DB=gameon_db

DB_SERVICE_URL=http://localhost:3000
```
### Step 2: Set Up the Redis Instance for Sessions

**Install Redis:**

```
docker run --name gameon-redis -p 6379:6379 -d redis redis-server --requirepass yourRedisPassword
```

**Access Redis CLI**

```
docker exec -it gameon-redis redis-cli
```

**Update Redis Configuration:**

Create or update your `.env` file with the following redis connection details:

```
REDIS_URL=redis://:yourRedisPassword@localhost:6379
```

### Step 3: Session Configurations

**Update Session Configuration:**

Create or update your `.env` file with the following express-session details:

```
SESSION_SECRET=yourSessionSecret
```

### Step 4: Install Dependencies

**Install Node.js:**

If you don't have Node.js installed, download and install it from the official website.

**Install Project Dependencies:**

Navigate to your project directory and run:
```bash
npm install
```

### Step 5: Start the Server

**Compile TypeScript:**

Run the TypeScript compiler to generate the JavaScript files:
```bash
npx tsc
```

**Start the Server:**

Run the compiled JavaScript files:
```bash
node dist/index.js
```

Alternatively, you can use ts-node to run the server directly without compiling:
```bash
npm start
# OR
npx ts-node src/index.ts
```

**Verify the Server:**

The server should start and log a message like:
```
Server is running on http://localhost:3000
```

### Step 6: Test the API Endpoints

You can use tools like Postman, cURL, or Thunder Client (VSCode extension) to test your API endpoints.

**Example: Create a User**

**Endpoint:** `POST /users`

**Request Body:**
```json
{
    "discord_id": "123456789012345678",
    "username": "gamer123",
    "email": "gamer123@example.com"
}
```

**Response:**
```json
{
    "discord_id": "123456789012345678",
    "username": "gamer123",
    "email": "gamer123@example.com",
    "created_at": "2023-10-01T12:00:00Z",
    "banned": false
}
```

**Example: Create a Game**

**Endpoint:** `POST /games`

**Request Body:**
```json
{
    "game_name": "League of Legends",
    "description": "A fast-paced, team-based MOBA where players control unique champions in 5v5 battles, aiming to destroy the enemy Nexus."
}
```

**Response:**
```json
{
    "game_id": 1,
    "game_name": "League of Legends",
    "description": "A fast-paced, team-based MOBA where players control unique champions in 5v5 battles, aiming to destroy the enemy Nexus."
}
```

**Example: Create Preferences**

**Endpoint:** `POST /preferences`

**Request Body:**
```json
{
    "discord_id": "123456789012345678",
    "spoken_language": "English",
    "time_zone": "UTC+1",
    "skill_level": "competitive",
    "game_id": 1
}
```

**Response:**
```json
{
    "preference_id": 1,
    "spoken_language": "English",
    "time_zone": "UTC+1",
    "skill_level": "competitive",
    "user": {
        "discord_id": "123456789012345678",
        "username": "gamer123",
        "email": "gamer123@example.com",
        "created_at": "2025-02-19T09:10:28.222Z",
        "banned": false
    },
    "game": {
        "game_id": 1,
        "game_name": "League of Legends",
        "description": "A fast-paced, team-based MOBA where players control unique champions in 5v5 battles, aiming to destroy the enemy Nexus."
    }
}
```

**Example: Create a Group**

**Endpoint:** `POST /groups`

**Request Body:**
```json
{
    "game_id": 1,
    "group_name": "LoL Competitive Group",
    "max_players": 5
}
```

**Response:**
```json
{
    "group_id": 1,
    "game": {
        "game_id": 1,
        "game_name": "League of Legends",
        "description": "A fast-paced, team-based MOBA where players control unique champions in 5v5 battles, aiming to destroy the enemy Nexus."
    },
    "group_name": "LoL Competitive Group",
    "created_at": "2023-10-01T12:00:00Z",
    "max_players": 5
}
```

### Step 7: Stop the Server

To stop the server, press `Ctrl + C` in the terminal where the server is running.

### Step 8: Clean Up

**Stop the Database:**

If you're using Docker, stop the MySQL container:
```bash
docker stop gameon-db
```

**Delete the Database (Optional):**

If you want to start fresh, you can drop the database:
```sql
DROP DATABASE gameon_db;
```