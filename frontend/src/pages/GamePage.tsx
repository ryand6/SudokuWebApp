import { SudokuBoard } from "@/components/game/SudokuBoard";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { useHandleGetGameError } from "@/hooks/game/useHandleGetGameError";
import { useValidateGameId } from "@/hooks/game/useValidateGameId";
import { useValidateLobbyUser } from "@/hooks/lobby/useValidateLobbyUser";
import { useGetGame } from "@/hooks/game/useGetGame";
import { useGetCurrentUser } from "@/hooks/users/useGetCurrentUser";
import type { PlayerColour } from "@/types/enum/PlayerColour";
import { setupPlayerGameStates } from "@/utils/game/setupPlayerGameStates";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export type CellState = {
  value: number | null;
  notes: number;
};

export type BoardState = CellState[][];

export type PlayerState = {
    id: number,
    name: string,
    colour: PlayerColour,
    boardState: BoardState
};

export function GamePage() {

    const { gameId } = useParams();

    const gameIdNum = gameId ? Number(gameId) : NaN;

    useValidateGameId(gameIdNum);

    // Store game state for current player
    const [playerGameState, setPlayerGameState] = useState<PlayerState>();

    // Store game state for all rival players
    const [rivalPlayerGameStates, setRivalPlayerGameStates] = useState<PlayerState[]>();

    // CREATE FUNCTION - gets game data from server, includes game state data for each player
    const {data: gameData, isLoading: isGameLoading, isError: isGameError, error: gameError} = useGetGame(gameIdNum);

    const {data: currentUser, isLoading: isCurrentUserLoading } = useGetCurrentUser();

    // IMPLEMENT
    //const leaveGameHandler = useLeaveGame();

    useHandleGetGameError(isGameError, gameError);

    // IMPLEMENT leaveGameHandler
    //useValidateLobbyUser(gameData, currentUser, leaveGameHandler.isLeaving);

    useEffect(() => {
        // CREATE FUNCTION - calls setGameState, filling the nested array with the values and notes from the server data for the current player
        setupPlayerGameStates(gameData, currentUser, setPlayerGameState, setRivalPlayerGameStates);

    }, [gameData, currentUser, setPlayerGameState, setRivalPlayerGameStates]);


    if (isGameLoading || isCurrentUserLoading) return <SpinnerButton />;

    console.log("GAME DATA: ", gameData);
    
    if (!gameData || !currentUser || !playerGameState || !rivalPlayerGameStates) return null;

    return (
        <div>
            GAME PAGE
            <SudokuBoard playerGameState={playerGameState} />
        </div>
    )
}