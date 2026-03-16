import { SudokuBoard } from "@/components/game/SudokuBoard";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { useHandleGetGameError } from "@/hooks/game/useHandleGetGameError";
import { useValidateGameId } from "@/hooks/game/useValidateGameId";
import { useGetGame } from "@/api/rest/game/query/useGetGame";
import { useGetCurrentUser } from "@/api/rest/users/query/useGetCurrentUser";
import { useNavigate, useParams } from "react-router-dom";
import { mapBoardToBlocks } from "@/utils/game/blockUtils";
import type { CellCoordinates, CellHighlightDetails, CellState } from "@/types/game/GameTypes";
import { useGetGamePlayerState } from "@/api/rest/game/query/useGetGamePlayerState";
import { useLeaveGame } from "@/api/rest/game/mutate/useLeaveGame";
import { useValidateGamePlayer } from "@/hooks/game/useValidateGamePlayer";
import { useHandleGameWsSubscriptions } from "@/hooks/game/useHandleGameWsSubscriptions";
import { useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { getGameHighlightedCells } from "@/api/rest/game/memory/query/getGameHighlightedCells";
import type { GameHighlightedCellsResponseDto } from "@/types/dto/response/GameHighlightedCellsResponseDto";
import { useGetGameHighlightedCells } from "@/hooks/game/useGetGameHighlightedCells";

export function GamePage() {

    const { gameId } = useParams();

    const gameIdNum = gameId ? Number(gameId) : NaN;

    useValidateGameId(gameIdNum);

    const [gameHighlightedCells, setGameHighlightedCells] = useState<Map<number, CellCoordinates> | undefined>(undefined);

    const queryClient = useQueryClient();
    const navigate = useNavigate();

    const {data: currentUser, isLoading: isCurrentUserLoading } = useGetCurrentUser();

    useGetGameHighlightedCells(gameIdNum, currentUser?.id, setGameHighlightedCells);
    

    // CREATE FUNCTION - gets game data from server, includes game state data for each player
    const {data: publicGameState, isLoading: isGameLoading, isError: isGameError, error: gameError} = useGetGame(gameIdNum);

    const {data: privateGameState, isLoading: isGameStateLoading, isError: isGameStateError, error: gameStateError} = useGetGamePlayerState(gameIdNum, currentUser?.id);

    const leaveGameHandler = useLeaveGame();

    useHandleGetGameError(isGameError, gameError);

    useValidateGamePlayer(publicGameState, currentUser, leaveGameHandler.isLeaving);

    useHandleGameWsSubscriptions(gameId, currentUser?.id, queryClient, navigate);

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
                <SudokuBoard 
                    userId={currentUser.id}
                    boardState={privateGameState.boardState} 
                    gamePlayers={publicGameState.players}
                    gameHighlightedCells={gameHighlightedCells} 
                />
            </div>
        </div>
    )
}