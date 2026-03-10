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
import { useGetGamePlayerState } from "@/api/rest/game/query/useGetGamePlayerState";
import { useLeaveGame } from "@/api/rest/game/mutate/useLeaveGame";
import { useValidateGamePlayer } from "@/hooks/game/useValidateGamePlayer";

export function GamePage() {

    const { gameId } = useParams();

    const gameIdNum = gameId ? Number(gameId) : NaN;

    useValidateGameId(gameIdNum);

    const {data: currentUser, isLoading: isCurrentUserLoading } = useGetCurrentUser();

    // CREATE FUNCTION - gets game data from server, includes game state data for each player
    const {data: publicGameState, isLoading: isGameLoading, isError: isGameError, error: gameError} = useGetGame(gameIdNum);

    const {data: privateGameState, isLoading: isGameStateLoading, isError: isGameStateError, error: gameStateError} = useGetGamePlayerState(gameIdNum, currentUser?.id);

    const leaveGameHandler = useLeaveGame();

    useHandleGetGameError(isGameError, gameError);

    useValidateGamePlayer(publicGameState, currentUser, leaveGameHandler.isLeaving);

    // IMPLEMENT useHandleGameWsSubscription

    if (isGameLoading || isCurrentUserLoading || isGameStateLoading) return <SpinnerButton />;

    console.log("GAME DATA: ", publicGameState);

    console.log("GAME STATE DATA: ", privateGameState);
    
    if (!publicGameState || !currentUser || !privateGameState) return null;

    // map board to sudoku blocks 
    //const sudokuBlocks: CellState[][] = mapBoardToBlocks(gameState.gameStates[currentUser.id]);

    return (
        <div>
            GAME PAGE
            <div className="flex justify-center">
                <SudokuBoard boardState={privateGameState.boardState} playerState={publicGameState.players[currentUser.id]} />
            </div>
        </div>
    )
}