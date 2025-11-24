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
        <div id="lobby-container">
            <div id="lobby-header">
                <h1 className="text-secondary font-bold text-shadow mb-3">{lobby?.lobbyName}</h1>
            </div>
            <div id="lobby-content" className="flex flex-col gap-4">
                <div id="mobile-tabs" className="md:hidden">
                    <Button onClick={() => setActivePanel("players")}>Players</Button>
                    <Button onClick={() => setActivePanel("settings")}>Settings</Button>
                    <Button onClick={() => setActivePanel("chat")}>Lobby Chat</Button>
                </div>
                <div className="md:flex md:flex-row">
                    <div className="md:flex md:flex-col md:flex-1">
                        <div className={`${activePanel === "players" ? "block" : "hidden"} md:block`}>
                            <LobbyPlayersPanel lobby={lobby} />
                        </div>

                        <div className={`${activePanel === "settings" ? "block" : "hidden"} md:block`}>
                            <LobbySettingsPanel lobby={lobby} currentUser={currentUser} />
                        </div>
                    </div>
                    <div className="md:flex md:flex-col md:flex-1">
                        <div className={`${activePanel === "chat" ? "block" : "hidden"} md:block`}>
                            <LobbyChatPanel lobby={lobby} currentUser={currentUser} />
                        </div>
                    </div> 
                </div>
            </div>
        </div>
    )
}