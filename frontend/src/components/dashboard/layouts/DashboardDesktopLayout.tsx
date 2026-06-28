import type { UserDto } from "@/types/dto/entity/user/UserDto"
import type { LobbyDetailsDto } from "@/types/dto/response/LobbyDetailsDto"
import { OnlinePlayWidget } from "../widgets/OnlinePlayWidget"
import { useState } from "react";
import { JoinLobbyModal } from "../JoinLobbyModal";
import { Modal } from "@/components/ui/custom/Modal";
import type { NavigateFunction } from "react-router-dom";
import { wordToProperCase } from "@/utils/string/wordToProperCase";
import { getDurationValue } from "@/utils/time/gameDurationUtils";
import { IconArrowBackUp, IconX } from "@tabler/icons-react";
import { LeaveGameAlertDialog } from "@/components/ui/custom/LeaveGameAlertDialog";
import type { UseMutateFunction } from "@tanstack/react-query";
import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";
import type { LeaveLobbyRequestDto } from "@/types/dto/request/LeaveLobbyRequestDto";
import type { LeaveGameRequestDto } from "@/types/dto/request/LeaveGameRequestDto";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import { LeaveLobbyAlertDialog } from "@/components/ui/custom/LeaveLobbyAlertDialog";
import { GameModesWidget } from "../widgets/GameModesWidget";
import { SinglePlayerWidget } from "../widgets/SinglePlayerWidget";

export function DashboardDesktopLayout({
    user,
    activeLobby,
    leaveLobbyHandler,
    leaveGameHandler,
    navigate
}: {
    user: UserDto,
    activeLobby: LobbyDetailsDto | undefined,
    leaveLobbyHandler: {
        mutate: UseMutateFunction<LobbyDto | null, Error, LeaveLobbyRequestDto, unknown>;
        isLeaving: boolean;
    },
    leaveGameHandler: {
        mutate: UseMutateFunction<GameDto | null, Error, LeaveGameRequestDto, unknown>;
        isLeaving: boolean;
    },
    navigate: NavigateFunction
}) {
    const iconSize = 24;

    const [isModalOpen, setModalOpen] = useState(false);
    const [isLeaveLobbyAlertOpen, setIsLeaveLobbyAlertOpen] = useState(false);
    const [isLeaveGameAlertOpen, setIsLeaveGameAlertOpen] = useState(false);

    const onRejoinClick = () => {
        if (!activeLobby) return;
        const path = activeLobby.inGame ? `/game/${activeLobby.currentGameId}` : `/lobby/${activeLobby.id}`;
        navigate(path);
    }
    
    return (
        <div className="flex flex-col w-full h-full font-display">
            <div className="flex items-center px-5 py-5 border-b-border border-b-2">
                <div className="tracking-wide font-semibold text-foreground text-lg">
                    Hello, {user.username}!
                </div>
            </div>
            <div className="flex flex-col p-5 gap-5 w-full">
                {
                    activeLobby && (
                        <div className="flex w-full items-center justify-between px-5 bg-app-gradient rounded-lg">
                            <div className="flex items-center w-full px-3 py-4 gap-4">
                                <div className="relative flex items-center justify-center w-6 h-6">
                                    <div className="absolute inset-0 rounded-full opacity-40 animate-pulse bg-primary" />
                                    <div className="w-2 h-2 rounded-full animate-pulse bg-primary" />
                                </div>
                                <div className="flex flex-col items-start">
                                    {activeLobby.currentGameId ? (
                                        <div className="tracking-wide font-semibold text-secondary-foreground text-lg">
                                            You are in an active game
                                        </div>
                                    ) : (
                                        <div className="tracking-wide font-semibold text-secondary-foreground text-lg">
                                            You are in an active lobby
                                        </div>
                                    )}
                                    
                                    <div className="text-secondary-foreground">
                                        {activeLobby.lobbyName} &middot; {wordToProperCase(activeLobby.gameType)} &middot; {wordToProperCase(activeLobby.gameMode)} &middot; {wordToProperCase(activeLobby.difficulty)} &middot; {wordToProperCase(getDurationValue(activeLobby.timeLimitPreset))}
                                    </div>
                                </div>
                            </div>
                            <div className="flex items-center gap-4">
                                <button
                                    className="flex items-center gap-1 px-4 py-3 rounded-lg bg-secondary-foreground/20 cursor-pointer hover:bg-secondary-foreground/10 text-semibold text-secondary-foreground text-lg border-1 border-sidebar"
                                    onClick={onRejoinClick}
                                >   
                                    <span>Rejoin</span>
                                    <span>
                                        <IconArrowBackUp size={iconSize} />
                                    </span>
                                </button>
                                <LeaveLobbyAlertDialog open={isLeaveLobbyAlertOpen} handleContinueClick={() => leaveLobbyHandler.mutate({ lobbyId: activeLobby.id })} setOpen={setIsLeaveLobbyAlertOpen} />
                                <LeaveGameAlertDialog open={isLeaveGameAlertOpen} handleContinueClick={() => leaveGameHandler.mutate({ gameId: activeLobby.currentGameId!, userId: user.id, lobbyId: activeLobby.id })} setOpen={setIsLeaveGameAlertOpen} />
                                <button
                                    className="flex items-center gap-1 px-4 py-3 rounded-lg bg-destructive/20 cursor-pointer hover:bg-destructive/10 text-semibold text-secondary-foreground text-lg border-1 border-sidebar"
                                    onClick={() => {
                                        activeLobby.currentGameId ? setIsLeaveGameAlertOpen(true) : setIsLeaveLobbyAlertOpen(true)
                                    }}
                                >   
                                    <span>Leave</span>
                                    <span>
                                        <IconX size={iconSize} />
                                    </span>
                                </button>
                            </div>
                        </div>
                    )
                }
                <div className="flex justify-evenly flex-1">
                    <div className="flex flex-col items-center w-full h-full gap-4">
                        <OnlinePlayWidget 
                            isMobile={false}
                            isActiveLobby={activeLobby ? true : false}
                            setModalOpen={setModalOpen}
                            navigate={navigate}
                        />
                        <SinglePlayerWidget
                            isMobile={false}    
                        />
                    </div>
                    <div className="flex flex-col items-center w-full h-full">

                    </div>
                </div>
                <div className="flex items-center w-full">
                    <GameModesWidget />
                </div>
            </div>
            <Modal isOpen={isModalOpen} onClose={() => setModalOpen(false)}><JoinLobbyModal isMobile={false} /></Modal>
        </div>
    )
}