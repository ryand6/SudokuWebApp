import { LobbyChatPanel } from "@/components/lobby/LobbyChatPanel";
import { LobbyPlayersPanel } from "@/components/lobby/LobbyPlayersPanel";
import { LobbySettingsPanel } from "@/components/lobby/LobbySettingsPanel";
import { Button } from "@/components/ui/button";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { TimerCountdown } from "@/components/ui/custom/TimerCountdown";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { useCheckIfUserInGame } from "@/hooks/game/useCheckIfUserInGame";
import { useGetLobby } from "@/hooks/lobby/useGetLobby";
import { useHandleGetLobbyError } from "@/hooks/lobby/useHandleGetLobbyError";
import { useLeaveLobby } from "@/hooks/lobby/useLeaveLobby";
import { useValidateLobbyId } from "@/hooks/lobby/useValidateLobbyId";
import { useValidateLobbyUser } from "@/hooks/lobby/useValidateLobbyUser";
import { useGetCurrentUser } from "@/hooks/users/useGetCurrentUser";
import { handleLobbyWebSocketMessages } from "@/services/websocket/handleLobbyWebSocketMessages";
import { getEpochTimeFromTimestamp } from "@/utils/time/getEpochTimeFromTimestamp";
import { useQueryClient } from "@tanstack/react-query";
import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

export function LobbyPage() {
    const { lobbyId } = useParams();

    const [activePanel, setActivePanel] = useState<"players" | "settings" | "chat">("players");

    const id = lobbyId ? Number(lobbyId) : NaN;

    useValidateLobbyId(id);

    const queryClient = useQueryClient();
    const navigate = useNavigate();

    const { subscribe, unsubscribe } = useWebSocketContext();
    
    const {data: lobby, isLoading: isLobbyLoading, isError: isLobbyError, error: lobbyError} = useGetLobby(id);
    const {data: currentUser, isLoading: isCurrentUserLoading } = useGetCurrentUser();
    const hasSubscribedRef = useRef(false);

    const leaveLobbyHandler = useLeaveLobby();

    useHandleGetLobbyError(isLobbyError, lobbyError);

    useValidateLobbyUser(lobby, currentUser);

    // Subscribe user to lobby websocket topic - ensures when page is refreshed or new session starts, user continues to receive lobby updates 
    useEffect(() => {
        if (!lobbyId || hasSubscribedRef.current) return;
        hasSubscribedRef.current = true;
        const lobbyIdNum = parseInt(lobbyId);
        const topic = `/topic/lobby/${lobbyId}`;
        const subscription = subscribe(topic, (body: any) => handleLobbyWebSocketMessages(body, queryClient, lobbyIdNum, navigate));

        return () => {
            if (subscription) unsubscribe(topic);
            hasSubscribedRef.current = false;
        };
    }, [lobbyId]);

    const gameQuery = useCheckIfUserInGame(
        lobby?.currentGameId ?? -1, // fallback to dummy id if lobby or game id not available
        currentUser?.id ?? -1    // fallback to dummy id if user not ready
    );

    // Handle navigation only when confirmed if user is part of current game
    useEffect(() => {
        if (!lobby || !currentUser || !lobby.inGame || !lobby.currentGameId) return;

        if (gameQuery.data) {
            navigate(`/game/${gameQuery.data.id}`);
        }
    }, [lobby, currentUser, gameQuery.data, navigate]);

    if (isLobbyLoading || isCurrentUserLoading) return <SpinnerButton />;

    if (!lobby || !currentUser) return null;

    const handleClick = () => {
        leaveLobbyHandler.mutate({ lobbyId: lobby.id });
    }

    return (
        <div id="lobby-container" className="flex flex-col flex-1">
            <div id="lobby-header" className="flex flex-row justify-between">
                <h1 className="text-secondary font-bold text-shadow m-3">{lobby?.lobbyName}</h1>
                {/* Show countdown timer to game start if it's running */}
                {lobby.countdownActive && lobby.countdownEndsAt && (
                    <TimerCountdown endTime={getEpochTimeFromTimestamp(lobby.countdownEndsAt)} />
                )}
                <Button className="m-2 cursor-pointer" onClick={handleClick}>Leave Lobby</Button>
            </div>
            <div id="lobby-content" className="flex flex-col flex-1 gap-4 max-h-[70vh] md:max-h-[75vh]">
                <div id="mobile-tabs" className="md:hidden">
                    <Button onClick={() => setActivePanel("players")}>Players</Button>
                    <Button onClick={() => setActivePanel("settings")}>Settings</Button>
                    <Button onClick={() => setActivePanel("chat")}>Lobby Chat</Button>
                </div>
                <div className="flex flex-row flex-1 min-h-0">
                    {/* Mobile only: render the active panel directly */}
                    <div className="flex-1 flex flex-col min-h-0 max-w-[95%] md:hidden m-5">
                        {activePanel === "players" && <LobbyPlayersPanel lobby={lobby} currentUser={currentUser} />}
                        {activePanel === "settings" && <LobbySettingsPanel lobby={lobby} currentUser={currentUser} />}
                        {activePanel === "chat" && <LobbyChatPanel lobby={lobby} currentUser={currentUser} />}
                    </div>

                    {/* Desktop only: split left/right columns */}
                    <div className="hidden md:flex flex-1 min-h-0">
                        {/* Left column */}
                        <div className="flex flex-col flex-1 min-h-0 max-w-[50%]">
                            <div className="flex flex-col flex-1 min-h-0">
                            <LobbyPlayersPanel lobby={lobby} currentUser={currentUser} />
                            </div>
                            <div className="flex flex-col flex-1 min-h-0">
                            <LobbySettingsPanel lobby={lobby} currentUser={currentUser} />
                            </div>
                        </div>
                        {/* Right column */}
                        <div className="flex flex-col flex-1 min-h-0 max-w-[50%]">
                            <LobbyChatPanel lobby={lobby} currentUser={currentUser} />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}