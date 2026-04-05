import { SudokuBoard } from "@/components/game/SudokuBoard";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { useHandleGetGameError } from "@/hooks/game/useHandleGetGameError";
import { useValidateGameId } from "@/hooks/game/useValidateGameId";
import { useGetGame } from "@/api/rest/game/query/useGetGame";
import { useGetCurrentUser } from "@/api/rest/users/query/useGetCurrentUser";
import { useNavigate, useParams } from "react-router-dom";
import type { CellCoordinates } from "@/types/game/GameTypes";
import { useGetGamePlayerState } from "@/api/rest/game/query/useGetGamePlayerState";
import { useLeaveGame } from "@/api/rest/game/mutate/useLeaveGame";
import { useValidateGamePlayer } from "@/hooks/game/useValidateGamePlayer";
import { useHandleGameWsSubscriptions } from "@/hooks/game/useHandleGameWsSubscriptions";
import { useQueryClient } from "@tanstack/react-query";
import { useMemo, useState } from "react";
import { getGameHighlightedCells } from "@/api/rest/game/memory/query/getGameHighlightedCells";
import type { GameHighlightedCellsResponseDto } from "@/types/dto/response/GameHighlightedCellsResponseDto";
import { useGetGameHighlightedCells } from "@/hooks/game/useGetGameHighlightedCells";
import { UserActionBar } from "@/components/game/UserActionBar";
import { GameNotificationLayer } from "@/components/game/GameNotificationLayer";
import { resolveBoardState } from "@/utils/game/gameUtils";
import { GameHUD } from "@/components/game/GameHUD";
import { getCellState } from "@/utils/game/boardStateUtils";
import type { PlayerColour } from "@/types/enum/PlayerColour";


export function GamePage() {

    const { gameId } = useParams();

    const gameIdNum = gameId ? Number(gameId) : NaN;

    useValidateGameId(gameIdNum);

    const [gameHighlightedCells, setGameHighlightedCells] = useState<Map<number, CellCoordinates> | undefined>(undefined);
    const [notesModeOn, setNotesModeOn] = useState<boolean>(false);

    const queryClient = useQueryClient();
    const navigate = useNavigate();

    const {data: currentUser, isLoading: isCurrentUserLoading } = useGetCurrentUser();

    useGetGameHighlightedCells(gameIdNum, currentUser?.id, setGameHighlightedCells);

    // CREATE FUNCTION - gets game data from server, includes game state data for each player
    const {data: publicGameState, isLoading: isGameLoading, isError: isGameError, error: gameError} = useGetGame(gameIdNum);

    const {data: privateGameState, isLoading: isGameStateLoading, isError: isGameStateError, error: gameStateError} = useGetGamePlayerState(gameIdNum, currentUser?.id);

    const boardState = useMemo(() => {
        return resolveBoardState(publicGameState?.gameMode, publicGameState?.sharedGameState.currentSharedBoardState, privateGameState?.boardState);
    }, [publicGameState?.gameMode, publicGameState?.sharedGameState.currentSharedBoardState, privateGameState?.boardState]);

    const playerColours: Record<number, PlayerColour> | undefined = useMemo(() => {
        if (!publicGameState) return;
            const newObj: Record<number, PlayerColour> = {};
            Object.keys(publicGameState.players).forEach((key) => newObj[Number(key)] = publicGameState.players[Number(key)].colour);
            return newObj;
    }, [publicGameState?.players]);

    const leaveGameHandler = useLeaveGame();

    useHandleGetGameError(isGameError, gameError);

    useValidateGamePlayer(publicGameState, currentUser, leaveGameHandler.isLeaving);

    useHandleGameWsSubscriptions(gameId, currentUser?.id, queryClient, navigate, setGameHighlightedCells);

    if (isGameLoading || isCurrentUserLoading || isGameStateLoading) return <SpinnerButton />;
    
    if (!publicGameState || !currentUser || !privateGameState || !boardState) return null;    

    const userHighlightedCell: CellCoordinates | undefined = gameHighlightedCells?.get(currentUser.id);

    console.log("PUBLIC GAME STATE", publicGameState);

    console.log("PRIVATE GAME STATE", privateGameState);

    console.log("BOARD STATE", boardState);

    return (
        <div className="flex flex-col h-screen">
            <GameNotificationLayer />
            <div className="flex justify-center items-center min-h-[500px] h-full py-[2%]">
                <div className="flex flex-col justify-center w-[80%] max-w-[1200px] h-full">
                    <GameHUD 
                        userId={currentUser.id}
                        gamePlayers={publicGameState.players} 
                        difficulty={publicGameState.difficulty}
                        gameMode={publicGameState.gameMode}
                        currentStreak={privateGameState.currentStreak} 
                    />
                    <SudokuBoard 
                        gameId={publicGameState.gameId}
                        userId={currentUser.id}
                        boardState={boardState} 
                        playerColours={playerColours!}
                        gamePlayers={publicGameState.players}
                        cellFirstOwnership={publicGameState.sharedGameState.cellFirstOwnership}
                        gameHighlightedCells={gameHighlightedCells}
                        setGameHighlightedCells={setGameHighlightedCells}
                        notesModeOn={notesModeOn}
                    />
                    <UserActionBar 
                        gameId={publicGameState.gameId}
                        userId={currentUser.id}
                        initialBoardState={publicGameState.initialBoardState}
                        playerHighlightedCell={userHighlightedCell}
                        highlightedCellState={userHighlightedCell ? getCellState(privateGameState.boardState, userHighlightedCell.row, userHighlightedCell.col) : undefined}
                        notesModeOn={notesModeOn}
                        setNotesModeOn={setNotesModeOn}
                        playerColours={playerColours!}
                        queryClient={queryClient}
                    />
                </div>
                
            </div>
        </div>
    )
}