import { SudokuBoard } from "@/components/game/SudokuBoard";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { useHandleGetGameError } from "@/hooks/game/useHandleGetGameError";
import { useValidateGameId } from "@/hooks/game/useValidateGameId";
import { useValidateLobbyUser } from "@/hooks/lobby/useValidateLobbyUser";
import { useGetGame } from "@/api/rest/game/query/useGetGame";
import { useGetCurrentUser } from "@/api/rest/users/query/useGetCurrentUser";
import { useParams } from "react-router-dom";
import { mapBoardToBlocks } from "@/utils/game/blockUtils";
import type { CellState } from "@/types/game/GameTypes";

export function GamePage() {

    const { gameId } = useParams();

    const gameIdNum = gameId ? Number(gameId) : NaN;

    useValidateGameId(gameIdNum);

    // CREATE FUNCTION - gets game data from server, includes game state data for each player
    const {data: gameState, isLoading: isGameLoading, isError: isGameError, error: gameError} = useGetGame(gameIdNum);

    const {data: currentUser, isLoading: isCurrentUserLoading } = useGetCurrentUser();

    // IMPLEMENT
    //const leaveGameHandler = useLeaveGame();

    useHandleGetGameError(isGameError, gameError);

    // IMPLEMENT leaveGameHandler
    //useValidateGamePlayeer(gameData, currentUser, leaveGameHandler.isLeaving);

    // IMPLEMENT useHandleGameWsSubscription

    if (isGameLoading || isCurrentUserLoading) return <SpinnerButton />;

    console.log("GAME DATA: ", gameState);
    
    if (!gameState || !currentUser) return null;


    // map board to sudoku blocks 
    const sudokuBlocks: CellState[][] = mapBoardToBlocks(gameState.gameStates[currentUser.id]);

    return (
        <div>
            GAME PAGE
            <SudokuBoard playerGameState={gameState.gameStates[currentUser.id]} />
        </div>
    )
}