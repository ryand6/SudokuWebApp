import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { useHandleGetGameError } from "@/hooks/game/useHandleGetGameError";
import { useValidateGameId } from "@/hooks/game/useValidateGameId";
import { useGetGame } from "@/api/rest/game/query/useGetGame";
import { useGetCurrentUser } from "@/api/rest/users/query/useGetCurrentUser";
import { useNavigate, useParams } from "react-router-dom";
import type { CellCoordinates, PrivateBoardState } from "@/types/game/GameTypes";
import { useGetGamePlayerState } from "@/api/rest/game/query/useGetGamePlayerState";
import { useLeaveGame } from "@/api/rest/game/mutate/useLeaveGame";
import { useValidateGamePlayer } from "@/hooks/game/useValidateGamePlayer";
import { useHandleGameWsSubscriptions } from "@/hooks/game/useHandleGameWsSubscriptions";
import { useQueryClient } from "@tanstack/react-query";
import { useMemo, useState } from "react";
import { useGetGameHighlightedCells } from "@/hooks/game/useGetGameHighlightedCells";
import { resolveBoardState } from "@/utils/game/gameUtils";
import type { PlayerColour } from "@/types/enum/PlayerColour";
import { useHandleClosedGame } from "@/hooks/game/useHandleClosedGame";
import { useShowGameResults } from "@/hooks/game/useShowGameResults";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { WaitingForPlayersScreen } from "@/components/game/screens/WaitingForPlayersScreen";
import { GameCountdownScreen } from "@/components/game/screens/GameCountdownScreen";
import { useConfirmPlayerGameLoaded } from "@/hooks/game/useConfirmPlayerGameLoaded";
import { useIsMobile } from "@/hooks/global/useIsMobile";
import { GameMobileLayout } from "@/components/game/layouts/GameMobileLayout";
import { GameDesktopLayout } from "@/components/game/layouts/GameDesktopLayout";

export function GamePage() {
    const { gameId } = useParams();
    const gameIdNum = gameId ? Number(gameId) : NaN;
    useValidateGameId(gameIdNum);

    const [gameHighlightedCells, setGameHighlightedCells] = useState<Map<number, CellCoordinates> | undefined>(undefined);
    const [notesModeOn, setNotesModeOn] = useState<boolean>(false);
    const [showGameResultsModal, setShowGameResultsModal] = useState<boolean>(false);

    const { send } = useWebSocketContext();

    const queryClient = useQueryClient();
    const navigate = useNavigate();

    const isMobile = useIsMobile();

    const {data: currentUser, isLoading: isCurrentUserLoading } = useGetCurrentUser();

    useGetGameHighlightedCells(gameIdNum, currentUser?.id, setGameHighlightedCells);

    // CREATE FUNCTION - gets game data from server, includes game state data for each player
    const {data: publicGameState, isLoading: isGameLoading, isError: isGameError, error: gameError} = useGetGame(gameIdNum);

    const {data: privateGameState, isLoading: isGameStateLoading, isError: isGameStateError, error: gameStateError} = useGetGamePlayerState(gameIdNum, currentUser?.id);

    // Redirect to lobby if game is in a closed state (closed, aborted)
    useHandleClosedGame(publicGameState?.gameStatus, publicGameState?.lobbyId, navigate);

    useConfirmPlayerGameLoaded(publicGameState, privateGameState, currentUser, send);

    const playerFinishedGame: boolean | undefined = currentUser ? publicGameState?.players[currentUser.id].finishedGame : undefined;

    useShowGameResults(playerFinishedGame, publicGameState?.endedPrematurely, setShowGameResultsModal);

    const boardState: PrivateBoardState | undefined = useMemo(() => {
        return resolveBoardState(publicGameState?.gameSettings.gameMode, publicGameState?.sharedGameState.currentSharedBoardState, privateGameState?.boardState);
    }, [publicGameState?.gameSettings.gameMode, publicGameState?.sharedGameState.currentSharedBoardState, privateGameState?.boardState]);

    const playerColours: Record<number, PlayerColour> | undefined = useMemo(() => {
        if (!publicGameState) return;
            const newObj: Record<number, PlayerColour> = {};
            publicGameState.playerIds.forEach((key) => newObj[Number(key)] = publicGameState.players[Number(key)].colour);
            return newObj;
    }, [publicGameState?.playerIds]);

    const leaveGameHandler = useLeaveGame();

    useHandleGetGameError(isGameError, gameError);

    useValidateGamePlayer(publicGameState, currentUser, leaveGameHandler.isLeaving);

    useHandleGameWsSubscriptions(gameId, currentUser?.id, currentUser?.userSettings.gameChatNotificationsEnabled, playerColours, queryClient, navigate, setGameHighlightedCells);

    if (isGameLoading || isCurrentUserLoading || isGameStateLoading) return <SpinnerButton />;
    
    if (!publicGameState || !currentUser || !privateGameState || !boardState) return null;    

    const userHighlightedCell: CellCoordinates | undefined = gameHighlightedCells?.get(currentUser.id);

    console.log("PUBLIC GAME STATE", publicGameState);
    console.log("PRIVATE GAME STATE", privateGameState);

    return (
        <div className="h-screen w-full">
            {
                publicGameState.gameStatus === "LOADING" ? (
                    <WaitingForPlayersScreen />
                ) : publicGameState.gameStatus === "COUNTDOWN" ? (
                    <GameCountdownScreen gameStartsAt={publicGameState.gameStartsAt} />
                ) : isMobile ? (
                    <GameMobileLayout
                        currentUser={currentUser}
                        publicGameState={publicGameState}
                        privateGameState={privateGameState}
                        boardState={boardState}
                        playerColours={playerColours}
                        userHighlightedCell={userHighlightedCell}
                        gameHighlightedCells={gameHighlightedCells}
                        setGameHighlightedCells={setGameHighlightedCells}
                        notesModeOn={notesModeOn}
                        setNotesModeOn={setNotesModeOn}
                        showGameResultsModal={showGameResultsModal}
                        leaveGameHandler={leaveGameHandler}
                        isMobile={true}
                        queryClient={queryClient}
                        navigate={navigate}
                    />
                ) : (
                    <GameDesktopLayout 
                        currentUser={currentUser}
                        publicGameState={publicGameState}
                        privateGameState={privateGameState}
                        boardState={boardState}
                        playerColours={playerColours}
                        userHighlightedCell={userHighlightedCell}
                        gameHighlightedCells={gameHighlightedCells}
                        setGameHighlightedCells={setGameHighlightedCells}
                        notesModeOn={notesModeOn}
                        setNotesModeOn={setNotesModeOn}
                        showGameResultsModal={showGameResultsModal}
                        leaveGameHandler={leaveGameHandler}
                        isMobile={false}
                        queryClient={queryClient}
                        navigate={navigate}
                    />
                )
            }            
        </div>
    )
}