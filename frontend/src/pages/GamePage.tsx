import { useGetCurrentUser } from "@/hooks/users/useGetCurrentUser";
import type { PlayerColour } from "@/types/enum/PlayerColour";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export type CellState = {
  value: number | null;
  notes: number;
};

export type BoardState = CellState[][];

export type PlayerState = {
    name: string,
    colour: PlayerColour,
    boardState: BoardState
};

export function GamePage() {

    const { gameId } = useParams();

    const gameIdNum = gameId ? Number(gameId) : NaN;

    // Store game state for current player
    const [gameState, setGameState] = useState<PlayerState>();

    // Store game state for all rival players
    const [rivalGameStates, setRivalGameStates] = useState<PlayerState[]>();

    // CREATE FUNCTION - gets game data from server, includes game state data for each player
    const {data: gameData, isLoading: isGameLoading, isError: isGameError, error: gameError} = useGetGame(gameIdNum);

    const {data: currentUser, isLoading: isCurrentUserLoading } = useGetCurrentUser();

    useEffect(() => {
        // CREATE FUNCTION - calls setGameState, filling the nested array with the values and notes from the server data for the current player
        storeCurrentUserGameState(gameData);
        // CREATE FUNCTION - calls setGameState, filling the nested array with the values and notes from the server data for the rival player(s)
        storeRivalUserGameStates(gameData);
    }, [gameData]);

    return (
        <div>
            GAME PAGE
        </div>
    )
}