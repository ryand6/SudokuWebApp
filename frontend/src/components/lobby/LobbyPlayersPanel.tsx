import { Button } from "../ui/button";
import type { LobbyPlayerDto } from "@/types/dto/entity/lobby/LobbyPlayerDto";
import { toast } from "react-toastify";
import type { LobbyStatus } from "@/types/enum/LobbyStatus";
import { useUpdateLobbyPlayerStatus } from "@/api/rest/lobby/player/mutate/useUpdateLobbyPlayerStatus";
import { ButtonCopy } from "../ui/custom/ButtonCopy";
import { useGetActiveUserTokens } from "@/api/rest/lobby/token/query/useGetActiveUserTokens";
import { useRefreshActiveTokensList } from "@/hooks/lobby/useRefreshActiveTokensList";
import { useState } from "react";
import { useRequestJoinCode } from "@/api/rest/lobby/token/mutate/useRequestJoinCode";
import { useQueryClient } from "@tanstack/react-query";
import { JoinCodeAlertDialog } from "../ui/custom/JoinCodeAlertDialog";

export function LobbyPlayersPanel({
    lobbyId, 
    userId,
    isPublic,
    hostId,
    lobbyPlayers,
    countdownActive
}: {
    lobbyId: number, 
    userId: number,
    isPublic: boolean,
    hostId: number,
    lobbyPlayers: LobbyPlayerDto[],
    countdownActive: boolean
}) {

    const [isAlertOpen, setIsAlertOpen] = useState<boolean>(false);
    const [isUrlFormat, setIsUrlFormat] = useState<boolean>(true);
    const requestJoinCodeMutation = useRequestJoinCode();
    const queryClient = useQueryClient();

    const joinUrl = window.location.origin + "/join/private/";

    const currentLobbyPlayer: LobbyPlayerDto | undefined = lobbyPlayers.find((lp) => lp.id.userId === userId);

    // Custom hook used to update lobby player status in backend and frontend cache
    const updateLobbyPlayerStatus = useUpdateLobbyPlayerStatus();

    const { data } = useGetActiveUserTokens(userId);
    const activeTokens = data?.activeTokens ?? [];
    
    // Interval to refresh active tokens display every minute
    useRefreshActiveTokensList(queryClient, userId);

    const handleRequestJoinCode = () => {
        if (activeTokens.length === 0) {
            requestJoinCode();
        } else {
            setIsAlertOpen(true);
        }
    }

    const requestJoinCode = async () => {
        await requestJoinCodeMutation.mutateAsync({ lobbyId: lobbyId, userId: userId });
    };

    // Create an array of undefined values the size of the number of player slots left to fill - used to indicate in UI how many player slots are left
    const playerSlotsRemaining = Array.from(
        { length: 4 - lobbyPlayers.length }
    );

    const handleToggleReady = () => {
        if (!currentLobbyPlayer) {
            toast.error("Lobby player does not exist", {containerId: "foreground"});
            return;
        }
        const updatedStatus: LobbyStatus = currentLobbyPlayer.lobbyStatus === "READY" ? "WAITING" : "READY";
        updateLobbyPlayerStatus.mutate({ lobbyId: lobbyId, userId: userId, lobbyStatus: updatedStatus });
    };

    const recentJoinCode = activeTokens?.[activeTokens.length - 1]?.token || ""
    const recentJoinCodeText = recentJoinCode ? 
        isUrlFormat ? joinUrl + recentJoinCode 
        : recentJoinCode
        : "";

    return (
        <div id="lobby-player-panel" className="flex flex-col flex-1 lobby-card">
            {!isPublic && <JoinCodeAlertDialog open={isAlertOpen} handleContinueClick={requestJoinCode} setOpen={setIsAlertOpen} />}
            <h2 className="card-header">Players ({lobbyPlayers.length}/4)</h2>
            {lobbyPlayers.sort((lobbyPlayerA, lobbyPlayerB) => lobbyPlayerA.user.username.localeCompare(lobbyPlayerB.user.username)).map((player, index) => {
                return (
                    <div id="player-row" key={index}>
                        {player.user.id === hostId && <span id="host-star">★</span>}
                        <span id="player-name">{player.user.username}</span>
                        {player.lobbyStatus === "READY" ? <span className="bg-[#c6f6d5] text-[#22543d]">Ready</span> : player.lobbyStatus === "INGAME" ? 
                        <span className="bg-[#bee3f8] text-[#2a69ac]">In Game</span> : <span className="bg-[#fed7d7] text-[#742a2a]">Waiting</span>}
                    </div>
                )
            })}
            {!countdownActive && playerSlotsRemaining.map((_, index) => {
                return (
                    <div id="empty-player-row" key={index}>
                        <span>Waiting for player...</span>
                    </div>
                )
            })}
            <Button 
                onClick={handleToggleReady} disabled={lobbyPlayers.length < 2}
                className="cursor-pointer"
            >
                Toggle Ready
            </Button>
            {!isPublic && lobbyPlayers.length < 4 &&
                <div className="mt-2">
                    <div className="flex justify-between py-3">
                        <div className="font-display text-muted-foreground font-semibold tracking-widest text-lg">
                            GENERATE JOIN CODE
                        </div>
                        <div className="flex p-1 gap-1 rounded-full border-muted border-1 bg-background">
                            <div 
                                className={`p-3 py-2 font-display rounded-full cursor-pointer text-sidebar-primary-foreground flex items-center gap-2
                                            ${!isUrlFormat && "bg-secondary! text-secondary-foreground! font-semibold!"}`}
                                onClick={() => setIsUrlFormat(false) }
                            >
                                <span>Code</span>
                            </div>
                            <div 
                                className={`p-3 py-2 font-display rounded-full cursor-pointer text-sidebar-primary-foreground flex items-center gap-2
                                            ${isUrlFormat && "bg-secondary! text-secondary-foreground! font-semibold!"}`}
                                onClick={() => setIsUrlFormat(true)} 
                            >
                                <span>URL</span>
                            </div>
                        </div>
                    </div>
                    <div className="flex flex-row items-center gap-4 mt-2">
                        <div className="flex flex-row justify-between w-[75%] h-[100%] border-1 py-1 px-2 text-gray-400">
                            <span className="truncate max-w-full">{recentJoinCodeText || "Generated join code"}</span>
                            <ButtonCopy text={recentJoinCodeText} className="text-black"/>
                        </div>
                        <Button id="join-private-btn" className="w-[20%] cursor-pointer whitespace-normal text-wrap" onClick={handleRequestJoinCode}>Generate Code</Button>
                    </div>
                    <div className="mt-2 max-h-40 border p-2 rounded overflow-y-auto">
                        <h2 className="border-b mb-2 py-1">Active Join Codes:</h2>
                        {activeTokens?.length ? (
                            activeTokens.map((token) => {
                                // Calculate the minutes left to expiry on the token, rounded up to the nearest minute
                                const minutesLeft = Math.max(0, Math.ceil((token.expiresAt - Date.now()) / 60000));
                                const joinCodeText = isUrlFormat ? joinUrl + "?" + token.token : token.token;
                                return (
                                    <div key={token.token} className="flex justify-between py-1 px-2 mb-2 border-b last:border-b-0">
                                        <span className="break-all w-[70%]">{joinCodeText}</span>
                                        <ButtonCopy text={joinCodeText} />
                                        <span className="text-gray-500">{minutesLeft} min left</span>
                                    </div>
                                );
                            })
                        ) : (
                            <span className="text-gray-400">No active join codes</span>
                        )}
                    </div>
                </div>
            }
        </div>
    )
}