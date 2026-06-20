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
import { IconCrown } from '@tabler/icons-react';
import { IconUserPlus } from '@tabler/icons-react';
import { Separator } from "../ui/separator";
import { IconLink } from '@tabler/icons-react';
import { IconRefresh } from '@tabler/icons-react';
import { SpinnerButton } from "../ui/custom/SpinnerButton";
import { useDotsAnimation } from "@/hooks/global/useDotsAnimation";

export function LobbyPlayersPanel({
    lobbyId, 
    userId,
    isPublic,
    hostId,
    lobbyPlayers,
    countdownActive,
    isMobile
}: {
    lobbyId: number, 
    userId: number,
    isPublic: boolean,
    hostId: number,
    lobbyPlayers: LobbyPlayerDto[],
    countdownActive: boolean,
    isMobile: boolean
}) {

    const [isAlertOpen, setIsAlertOpen] = useState<boolean>(false);
    const [isUrlFormat, setIsUrlFormat] = useState<boolean>(true);
    const requestJoinCodeMutation = useRequestJoinCode();
    const queryClient = useQueryClient();

    const iconSize: number = isMobile ? 16 : 24;
    const iconStroke: number = isMobile ? 2 : 3;

    const joinUrl = window.location.origin + "/join/private/";

    const currentLobbyPlayer: LobbyPlayerDto | undefined = lobbyPlayers.find((lp) => lp.id.userId === userId);

    // Custom hook used to update lobby player status in backend and frontend cache
    const updateLobbyPlayerStatus = useUpdateLobbyPlayerStatus();

    const { data } = useGetActiveUserTokens(userId);
    const activeTokens = data?.activeTokens ?? [];

    const dotsCount = useDotsAnimation();
    
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
        <div id="lobby-player-panel" className="flex flex-col flex-1 w-full gap-3 py-4 px-4 bg-background min-h-0 overflow-y-auto">
            {!isPublic && <JoinCodeAlertDialog open={isAlertOpen} handleContinueClick={requestJoinCode} setOpen={setIsAlertOpen} />}
            {lobbyPlayers.sort((lobbyPlayerA, lobbyPlayerB) => lobbyPlayerA.user.username.localeCompare(lobbyPlayerB.user.username)).map((player, index) => {
                return (
                    <div id="player-row" 
                        className={`flex items-center justify-between py-2 px-4 rounded-lg border-muted border-2 bg-muted/20 ${userId === Number(player.id.userId) && "bg-sidebar-primary/20! border-sidebar-primary!"}`} 
                        key={index}
                    >   
                        <div className="flex gap-2 items-center">
                            <span id="player-name" className="font-display font-semibold text-lg">{player.user.username}</span>
                            {player.user.id === hostId && 
                                <span id="host-star" className="text-sidebar-primary">
                                    <IconCrown size={iconSize} stroke={iconStroke} />
                                </span>
                            }
                        </div>
                        <div>
                            {
                                player.lobbyStatus === "READY" ? 
                                    (<span className="bg-[#c6f6d5] text-[#22543d] px-3 py-1 rounded-full font-display font-semibold">Ready</span>) 
                                : player.lobbyStatus === "INGAME" ? 
                                    (<span className="bg-[#bee3f8] text-[#2a69ac] px-3 py-1 rounded-full font-display font-semibold">In Game</span>) 
                                : 
                                    (<span className="bg-[#fed7d7] text-[#742a2a] px-3 py-1 rounded-full font-display font-semibold">Waiting</span>)
                            }
                        </div>
                    </div>
                )
            })}
            {!countdownActive && playerSlotsRemaining.map((_, index) => {
                return (
                    <div 
                        id="empty-player-row"
                        className="flex items-center gap-2 py-2 px-4 rounded-lg border-muted border-2 border-dashed"
                        key={index}
                    >
                        <span className="text-muted">
                            <IconUserPlus size={iconSize} />
                        </span>
                        <span className="text-sm italic text-muted">
                            Waiting for player
                            <span className={dotsCount >= 1 ? "opacity-100" : "opacity-0"}>.</span>
                            <span className={dotsCount >= 2 ? "opacity-100" : "opacity-0"}>.</span>
                            <span className={dotsCount >= 3 ? "opacity-100" : "opacity-0"}>.</span>
                        </span>
                    </div>
                )
            })}
            <Button 
                onClick={handleToggleReady} disabled={lobbyPlayers.length < 2}
                className="cursor-pointer font-display text-lg"
            >
                Toggle Ready
            </Button>
            {!isPublic && lobbyPlayers.length < 4 &&
            <>
                <Separator orientation="horizontal" className="bg-muted border-1 border-muted" />
                <div className="flex flex-col items-center justify-center gap-3">
                    <div className="flex justify-between py-3 w-full">
                        <div className="flex font-display items-center gap-2 text-muted-foreground font-semibold tracking-widest text-lg">
                            <span>
                                <IconLink size={iconSize} stroke={iconStroke} />
                            </span>
                            <span>
                                INVITE A FRIEND
                            </span>
                        </div>
                        <div className="flex p-1 gap-1 rounded-full border-muted border-1 bg-background">
                            <div 
                                className={`px-3 py-1 font-display rounded-full cursor-pointer text-sidebar-primary-foreground flex items-center gap-2
                                            ${!isUrlFormat && "bg-secondary! text-secondary-foreground! font-semibold!"}`}
                                onClick={() => setIsUrlFormat(false) }
                            >
                                <span>Code</span>
                            </div>
                            <div 
                                className={`px-3 py-1 font-display rounded-full cursor-pointer text-sidebar-primary-foreground flex items-center gap-2
                                            ${isUrlFormat && "bg-secondary! text-secondary-foreground! font-semibold!"}`}
                                onClick={() => setIsUrlFormat(true)} 
                            >
                                <span>URL</span>
                            </div>
                        </div>
                    </div>
                    {requestJoinCodeMutation.isPending && <SpinnerButton />}
                    <div className="flex flex-row items-center w-full">
                        <div className="flex flex-row w-full justify-between items-center border-2 rounded-lg py-1 px-2 border-muted">
                            <span 
                                className={`truncate max-w-full font-display ${recentJoinCodeText ? "text-accent-foreground" : "text-muted"}`}
                            >
                                {recentJoinCodeText || "Generated join code"}
                            </span>
                            <ButtonCopy text={recentJoinCodeText} className="text-black"/>
                        </div> 
                    </div>
                    <Button 
                        id="join-private-btn" 
                        className="w-full font-display text-lg cursor-pointer whitespace-normal text-wrap" 
                        onClick={handleRequestJoinCode}
                    >
                        <span><IconRefresh size={iconSize} stroke={iconStroke} /></span>
                        <span>Generate new code</span>
                    </Button>
                    <div className="w-full border-muted border-2 py-2 rounded-lg overflow-y-auto">
                        <h2 className="border-muted border-b-2 px-2 mb-2 py-1 text-muted-foreground font-display font-semibold tracking-widest text-md">ACTIVE CODES</h2>
                        {activeTokens?.length ? (
                            activeTokens.map((token) => {
                                // Calculate the minutes left to expiry on the token, rounded up to the nearest minute
                                const minutesLeft = Math.max(0, Math.ceil((token.expiresAt - Date.now()) / 60000));
                                const joinCodeText = isUrlFormat ? joinUrl + "?" + token.token : token.token;
                                return (
                                    <div 
                                        key={token.token} 
                                        className="flex justify-between items-center font-display px-2 py-2 border-b last:border-b-0"
                                    >
                                        <span className="truncate w-[70%] text-accent-foreground">{joinCodeText}</span>
                                        <ButtonCopy text={joinCodeText} isCompact={true} />
                                        <span className="text-muted-foreground text-sm w-[10%]">{minutesLeft} min</span>
                                    </div>
                                );
                            })
                        ) : (
                            <span className="px-2 text-muted font-displaye">No active join codes</span>
                        )}
                    </div>
                </div>
            </>
                
            }
        </div>
    )
}