import { LobbyChatPanel } from "@/components/lobby/LobbyChatPanel";
import { LobbyPlayersPanel } from "@/components/lobby/LobbyPlayersPanel";
import { LobbySettingsPanel } from "@/components/lobby/LobbySettingsPanel";
import { Button } from "@/components/ui/button";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { useGetLobby } from "@/hooks/lobby/useGetLobby";
import { useHandleGetLobbyError } from "@/hooks/lobby/useHandleGetLobbyError";
import { useValidateLobbyId } from "@/hooks/lobby/useValidateLobbyId";
import { useValidateLobbyUser } from "@/hooks/lobby/useValidateLobbyUser";
import { useGetCurrentUser } from "@/hooks/users/useGetCurrentUser";
import { handleLobbyWebSocketMessages } from "@/services/websocket/handleLobbyWebSocketMessages";
import { useQueryClient } from "@tanstack/react-query";
import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { toast } from "react-toastify";

export function LobbyPage() {
    const { lobbyId } = useParams();

    const [activePanel, setActivePanel] = useState<"players" | "settings" | "chat">("players");

    const id = lobbyId ? Number(lobbyId) : NaN;

    useValidateLobbyId(id);

    const queryClient = useQueryClient();
    const { subscribe, unsubscribe } = useWebSocketContext();
    
    const {data: lobby, isLoading: isLobbyLoading, isError: isLobbyError, error: lobbyError} = useGetLobby(id);
    const {data: currentUser, isLoading: isCurrentUserLoading } = useGetCurrentUser();
    const hasSubscribedRef = useRef(false);

    useHandleGetLobbyError(isLobbyError, lobbyError);

    useValidateLobbyUser(lobby, currentUser);

    // Subscribe user to lobby websocket topic - ensures when page is refreshed or new session starts, user continues to receive lobby updates 
    useEffect(() => {
        if (!lobbyId || hasSubscribedRef.current) return;
        hasSubscribedRef.current = true;
        const lobbyIdNum = parseInt(lobbyId);
        const topic = `/topic/lobby/${lobbyId}`;
        const subscription = subscribe(topic, (body: any) => handleLobbyWebSocketMessages(body, queryClient, lobbyIdNum));

        console.log(subscription);

        return () => {
            if (subscription) unsubscribe(topic);
            hasSubscribedRef.current = false;
        };
    }, [lobbyId]);
    
    if (isLobbyLoading || isCurrentUserLoading) return <SpinnerButton />;

    if (!lobby || !currentUser) {
        toast.error("An error has occurred.");
        return null;
    }

    return (
        <div id="lobby-container" className="flex flex-col flex-1">
            <div id="lobby-header">
                <h1 className="text-secondary font-bold text-shadow mb-3">{lobby?.lobbyName}</h1>
            </div>
            <div id="lobby-content" className="flex flex-col flex-1 gap-4">
                <div id="mobile-tabs" className="md:hidden">
                    <Button onClick={() => setActivePanel("players")}>Players</Button>
                    <Button onClick={() => setActivePanel("settings")}>Settings</Button>
                    <Button onClick={() => setActivePanel("chat")}>Lobby Chat</Button>
                </div>
                <div className="flex flex-row flex-1">
                    {/* Mobile only: render the active panel directly */}
                    <div className="flex-1 flex flex-col min-h-0 md:hidden m-5">
                        {activePanel === "players" && <LobbyPlayersPanel lobby={lobby} />}
                        {activePanel === "settings" && <LobbySettingsPanel lobby={lobby} currentUser={currentUser} />}
                        {activePanel === "chat" && <LobbyChatPanel lobby={lobby} currentUser={currentUser} />}
                    </div>

                    {/* Desktop only: split left/right columns */}
                    <div className="hidden md:flex flex-1 min-h-0">
                        {/* Left column */}
                        <div className="flex flex-col flex-1 min-h-0">
                            <div className="flex flex-col flex-1 min-h-0">
                            <LobbyPlayersPanel lobby={lobby} />
                            </div>
                            <div className="flex flex-col flex-1 min-h-0">
                            <LobbySettingsPanel lobby={lobby} currentUser={currentUser} />
                            </div>
                        </div>
                        {/* Right column */}
                        <div className="flex flex-col flex-1 min-h-0">
                            <LobbyChatPanel lobby={lobby} currentUser={currentUser} />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}