import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { useCheckIfUserInGame } from "@/api/rest/game/query/useCheckIfUserInGame";
import { useGetLobby } from "@/api/rest/lobby/query/useGetLobby";
import { useHandleGetLobbyError } from "@/hooks/lobby/useHandleGetLobbyError";
import { useHandleLobbyWsSubscription } from "@/hooks/lobby/useHandleLobbyWsSubscription";
import { useLeaveLobby } from "@/api/rest/lobby/mutate/useLeaveLobby";
import { useNavigateUserWhenInGame } from "@/hooks/lobby/useNavigateUserWhenInGame";
import { useValidateLobbyId } from "@/hooks/lobby/useValidateLobbyId";
import { useValidateLobbyUser } from "@/hooks/lobby/useValidateLobbyUser";
import { useGetCurrentUser } from "@/api/rest/users/query/useGetCurrentUser";
import { useQueryClient } from "@tanstack/react-query";
import { useNavigate, useParams } from "react-router-dom";
import type { LobbyPlayerDto } from "@/types/dto/entity/lobby/LobbyPlayerDto";
import { useIsMobile } from "@/hooks/global/useIsMobile";
import { LobbyMobileLayout } from "@/components/lobby/layouts/LobbyMobileLayout";
import { LobbyDesktopLayout } from "@/components/lobby/layouts/LobbyDesktopLayout";

export function LobbyPage() {
    const { lobbyId } = useParams();

    const lobbyIdNum = lobbyId ? Number(lobbyId) : NaN;

    useValidateLobbyId(lobbyIdNum);

    const queryClient = useQueryClient();
    const navigate = useNavigate();

    const isMobile = useIsMobile();
    
    const {data: lobby, isLoading: isLobbyLoading, isError: isLobbyError, error: lobbyError} = useGetLobby(lobbyIdNum);
    const {data: currentUser, isLoading: isCurrentUserLoading } = useGetCurrentUser();

    const leaveLobbyHandler = useLeaveLobby();

    useHandleGetLobbyError(isLobbyError, lobbyError);

    useValidateLobbyUser(lobby, currentUser, leaveLobbyHandler.isLeaving);

    // Handle subscribing/unsubscribing to Lobby topic on mount/unmount
    useHandleLobbyWsSubscription(lobbyId, queryClient, navigate);

    // check if user is a player in the current game
    const { data: gameQueryData, isLoading: isLoadingGame, error: isUserInGameError } = useCheckIfUserInGame(
        // Provide -1 default values if required parameters not available - will result in query not running until proper values are provided
        lobby?.currentGameId ?? -1
    );

    const lobbyPlayer: LobbyPlayerDto | undefined = lobby?.lobbyPlayers.filter(player => player.id.userId === currentUser?.id).at(0);
    const playerInGame: boolean | null = lobbyPlayer ? lobbyPlayer.lobbyStatus === "INGAME" : null;

    // Navigates user to game page when they are in an active game
    useNavigateUserWhenInGame(lobby?.inGame, lobby?.currentGameId, currentUser?.id, playerInGame, gameQueryData?.gameId, isLoadingGame, navigate);

    if (isLobbyLoading || isCurrentUserLoading) return <SpinnerButton />;

    if (!lobby || !currentUser) return null;

    const handleLeaveLobbyClick = () => {
        leaveLobbyHandler.mutate({ lobbyId: lobby.id });
    }

    return isMobile ? 
        <LobbyMobileLayout
            lobbyId={lobby.id}
            lobbyName={lobby.lobbyName}
            userId={currentUser.id}
            difficulty={lobby.lobbySettings.difficulty}
            isPublic={lobby.lobbySettings.isPublic}
            hostId={lobby.host.id}
            countdownActive={lobby.lobbyCountdown.countdownActive}
            countdownEndsAt={lobby.lobbyCountdown.countdownEndsAt}
            timeLimit={lobby.lobbySettings.timeLimit}
            gameMode={lobby.lobbySettings.gameMode}
            gameType={lobby.lobbySettings.gameType}
            lobbyPlayers={lobby.lobbyPlayers} 
            isMobile={isMobile}
            handleLeaveLobbyClick={handleLeaveLobbyClick}
        /> : 
        <LobbyDesktopLayout
            lobbyId={lobby.id}
            lobbyName={lobby.lobbyName}
            userId={currentUser.id}
            difficulty={lobby.lobbySettings.difficulty}
            isPublic={lobby.lobbySettings.isPublic}
            hostId={lobby.host.id}
            countdownActive={lobby.lobbyCountdown.countdownActive}
            countdownEndsAt={lobby.lobbyCountdown.countdownEndsAt}
            timeLimit={lobby.lobbySettings.timeLimit}
            gameMode={lobby.lobbySettings.gameMode}
            gameType={lobby.lobbySettings.gameType}
            lobbyPlayers={lobby.lobbyPlayers}
            isMobile={isMobile}
            handleLeaveLobbyClick={handleLeaveLobbyClick}
        />

}