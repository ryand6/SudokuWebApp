-- Drop tables if they already exist
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS scores CASCADE;
DROP TABLE IF EXISTS sudoku_puzzles CASCADE;
DROP TABLE IF EXISTS lobbies CASCADE;
DROP TABLE IF EXISTS lobby_state CASCADE;

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
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    total_score INTEGER DEFAULT 0,
    games_played INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
    lobby_id INTEGER REFERENCES lobbies(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    puzzle_id INTEGER REFERENCES sudoku_puzzles(id) ON DELETE CASCADE,
    current_board_state VARCHAR(81) NOT NULL,
    score INTEGER DEFAULT 0,
    last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Used to handle auto updating of scores 'updated_at' timestamps
CREATE OR REPLACE FUNCTION update_scores_timestamp()
RETURNS TRIGGER AS '
BEGIN
    NEW.updated_at = clock_timestamp();
    RETURN NEW;
END;
'
LANGUAGE plpgsql;

-- Used to handle auto updating of lobby state 'last_active' timestamps
CREATE OR REPLACE FUNCTION update_lobby_state_timestamp()
RETURNS TRIGGER AS '
BEGIN
    NEW.last_active = clock_timestamp();
    RETURN NEW;
END;
'
LANGUAGE plpgsql;

CREATE TRIGGER update_scores_updated_at
BEFORE UPDATE ON scores
FOR EACH ROW
EXECUTE FUNCTION update_scores_timestamp();


CREATE TRIGGER update_lobby_state_last_active
BEFORE UPDATE ON lobby_state
FOR EACH ROW
EXECUTE FUNCTION update_lobby_state_timestamp();
