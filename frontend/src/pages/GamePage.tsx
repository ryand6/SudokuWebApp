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
import { mapBoardToBlocks } from "@/utils/game/blockUtils";

export type CellState = {
    value: string | undefined;
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
        // Calls setGameState, filling the nested array with the values and notes from the server data for the current player
        try {
            setupPlayerGameStates(gameData, currentUser, setPlayerGameState, setRivalPlayerGameStates);
        } catch (error) {
            console.log("Error setting up player game states: ", error);
        }
    }, [gameData, currentUser, setPlayerGameState, setRivalPlayerGameStates]);


    if (isGameLoading || isCurrentUserLoading) return <SpinnerButton />;

    console.log("GAME DATA: ", gameData);

    console.log("Current Player State: ", playerGameState);

    console.log("Rival Player States: ", rivalPlayerGameStates);
    
    if (!gameData || !currentUser || !playerGameState || !rivalPlayerGameStates) return null;


    // Get array of blocks
    const sudokuBlocks: CellState[][] = mapBoardToBlocks(playerGameState.boardState);

    return (
        <div>
            GAME PAGE
            <SudokuBoard playerGameState={playerGameState} />
        </div>
    )
}