import { SudokuBoard } from "@/components/game/SudokuBoard";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { useHandleGetGameError } from "@/hooks/game/useHandleGetGameError";
import { useValidateGameId } from "@/hooks/game/useValidateGameId";
import { useValidateLobbyUser } from "@/hooks/lobby/useValidateLobbyUser";
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

    useValidateGameId(gameIdNum);

    // Store game state for current player
    const [gameState, setGameState] = useState<PlayerState>();

    // Store game state for all rival players
    const [rivalGameStates, setRivalGameStates] = useState<PlayerState[]>();

    // CREATE FUNCTION - gets game data from server, includes game state data for each player
    const {data: gameData, isLoading: isGameLoading, isError: isGameError, error: gameError} = useGetGame(gameIdNum);

    const {data: currentUser, isLoading: isCurrentUserLoading } = useGetCurrentUser();

    const leaveGameHandler = useLeaveGame();

    useHandleGetGameError(isGameError, gameError);

    useValidateLobbyUser(gameData, currentUser, leaveGameHandler.isLeaving);

    useEffect(() => {
        // CREATE FUNCTION - calls setGameState, filling the nested array with the values and notes from the server data for the current player
        storeCurrentUserGameState(gameData);
        // CREATE FUNCTION - calls setGameState, filling the nested array with the values and notes from the server data for the rival player(s)
        storeRivalUserGameStates(gameData);
    }, [gameData]);


    if (isGameLoading || isCurrentUserLoading) return <SpinnerButton />;
    
    if (!gameData || !currentUser) return null;

    return (
        <div>
            GAME PAGE
            <SudokuBoard gameState={gameState} />
        </div>
    )
}