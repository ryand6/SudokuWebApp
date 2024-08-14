-- Drop tables if they already exist
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS scores;
DROP TABLE IF EXISTS sudoku_puzzles;
DROP TABLE IF EXISTS lobbies;
DROP TABLE IF EXISTS lobby_state;

-- Create 'users' table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create 'scores' table
CREATE TABLE scores (
    id SERIAL PRIMARY KEY,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    total_score INTEGER DEFAULT 0,
    games_played INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATED CURRENT_TIMESTAMP
);

-- Create 'sudoku_puzzles' table used for storing generated puzzles, their solutions and their difficulty rating
CREATE TABLE sudoku_puzzles (
    id SERIAL PRIMARY KEY,
    initial_board_state VARCHAR(81) NOT NULL,
    solution VARCHAR(81) NOT NULL,
    difficulty VARCHAR(50) CHECK (difficulty IN ('easy', 'medium', 'hard', 'extreme'))
);

-- Create 'lobbies' table
CREATE TABLE lobbies (
    id SERIAL PRIMARY KEY,
    lobby_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Create the 'lobby_state' table used to store the game state corresponding to a lobby and generated puzzle for individual users
-- Consider overwriting user info for a lobby if they generate a new puzzle as only the active game state needs to be stored
CREATE TABLE lobby_state (
    id SERIAL PRIMARY KEY,
    FOREIGN KEY(lobby_id) REFERENCES lobbies(id) ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(puzzle_id) REFERENCES sudoku_puzzles(id) ON DELETE CASCADE,
    current_board_state VARCHAR(81) NOT NULL,
    score INTEGER DEFAULT 0,
    last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
