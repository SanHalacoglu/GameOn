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
    MYSQL_HOST=mysql
    MYSQL_PORT=3306
    MYSQL_USER=gameon_user
    MYSQL_PASSWORD=gameon_password
    MYSQL_DB=gameon_db
    SESSION_SECRET=yourSessionSecret
    REDIS_URL=redis://redis:6379
    ```
3. **Run Docker Compose:**
    In the root directory of the project:
    ```
    docker-compose up --build -d    
    ```
    Once this completes the server should be online and accessible.

    To take it down, run:

    ```
    docker-compose down -v
    ```

    the -v flag will clear the volumes associated with the SQL DB, leave it out if you want to keep that data.

## Example Folder
There is an example_frontend folder that replicates a simple frontend. Open the html file in your browser and make sure the backend is running

### Step : Test the API Endpoints

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