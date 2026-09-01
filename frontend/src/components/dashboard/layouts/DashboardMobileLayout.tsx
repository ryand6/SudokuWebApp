import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";
import type { UserDto } from "@/types/dto/entity/user/UserDto"
import type { LeaveGameRequestDto } from "@/types/dto/request/LeaveGameRequestDto";
import type { LeaveLobbyRequestDto } from "@/types/dto/request/LeaveLobbyRequestDto";
import type { LobbyDetailsDto } from "@/types/dto/response/LobbyDetailsDto";
import type { UseMutateFunction } from "@tanstack/react-query";
import { useState, type Dispatch, type SetStateAction } from "react";
import type { NavigateFunction } from "react-router-dom";
import { OnlinePlayWidget } from "../widgets/OnlinePlayWidget";
import { MyStatsWidget } from "../widgets/MyStatsWidget";
import { TopFiveLeaderboardsWidget } from "../widgets/TopFiveLeaderboardsWidget";
import { SinglePlayerWidget } from "../widgets/SinglePlayerWidget";
import { GameModesWidget } from "../widgets/GameModesWidget";
import { Modal } from "@/components/ui/custom/Modal";
import { JoinLobbyModal } from "../JoinLobbyModal";
import { IconArrowBackUp, IconChartBar, IconDeviceGamepad, IconInfoCircle, IconX } from "@tabler/icons-react";
import { LeaveGameAlertDialog } from "@/components/ui/custom/LeaveGameAlertDialog";
import { wordToProperCase } from "@/utils/string/wordToProperCase";
import { getDurationValue } from "@/utils/time/gameDurationUtils";
import { LeaveLobbyAlertDialog } from "@/components/ui/custom/LeaveLobbyAlertDialog";

export function DashboardMobileLayout({
    user,
    activeLobby,
    leaveLobbyHandler,
    leaveGameHandler,
    isJoinLobbyModalOpen,
    setJoinLobbyModalOpen,
    isLeaveLobbyAlertOpen,
    setIsLeaveLobbyAlertOpen,
    isLeaveGameAlertOpen,
    setIsLeaveGameAlertOpen,
    onRejoinClick,
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
    isJoinLobbyModalOpen: boolean,
    setJoinLobbyModalOpen: Dispatch<SetStateAction<boolean>>,
    isLeaveLobbyAlertOpen: boolean,
    setIsLeaveLobbyAlertOpen: Dispatch<SetStateAction<boolean>>,
    isLeaveGameAlertOpen: boolean,
    setIsLeaveGameAlertOpen: Dispatch<SetStateAction<boolean>>,
    onRejoinClick: () => void,   
    navigate: NavigateFunction
}) {
    const iconSize = 16;

    type tabOptions = "play" | "stats" | "info";

    const [tab, setTab] = useState<tabOptions>("play");

    return (
        <div className="flex flex-col w-full h-full font-display">
            <div className="w-full bg-sidebar flex pb-1 text-muted">
                <div
                    className={`flex flex-1 justify-center gap-2 items-center py-2
                        ${tab === "play" ? "border-b-2 border-sidebar-primary text-sidebar-primary" : ""}
                    `}
                    onClick={() => setTab("play")}
                >
                    <span><IconDeviceGamepad size={iconSize} /></span>
                    <span>Play</span>
                </div>
                <div
                    className={`flex flex-1 justify-center gap-2 items-center py-2
                        ${tab === "stats" ? "border-b-2 border-sidebar-primary text-sidebar-primary" : ""}
                    `}
                    onClick={() => setTab("stats")}
                >
                    <span><IconChartBar size={iconSize} /></span>
                    <span>Stats</span>
                </div>
                <div
                    className={`flex flex-1 justify-center gap-2 items-center py-2
                        ${tab === "info" ? "border-b-2 border-sidebar-primary text-sidebar-primary" : ""}
                    `}
                    onClick={() => setTab("info")}
                >
                    <span><IconInfoCircle size={iconSize} /></span>
                    <span>Info</span>
                </div>
            </div>
            {/* <div className="flex items-center px-5 py-5 border-b-border border-b-2">
                <div className="tracking-wide font-semibold text-foreground text-lg">
                    Hello, {user.username}!
                </div>
            </div> */}
            <div className="flex flex-col items-center w-full h-full gap-4 p-5">
                {
                    tab === "play" ? (
                        <>
                            {
                                activeLobby && (
                                    <div className="flex w-full items-center justify-between px-2 bg-app-gradient rounded-lg">
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
                                        <div className="flex flex-col items-center gap-2 py-2">
                                            <button
                                                className="flex items-center gap-1 px-3 py-2 rounded-lg bg-secondary-foreground/20 cursor-pointer hover:bg-secondary-foreground/10 text-semibold text-secondary-foreground text-md border-1 border-sidebar"
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
                                                className="flex items-center gap-1 px-3 py-2 rounded-lg bg-destructive/20 cursor-pointer hover:bg-destructive/10 text-semibold text-secondary-foreground text-md border-1 border-sidebar"
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
                            <OnlinePlayWidget 
                                isMobile={true}
                                isActiveLobby={activeLobby ? true : false}
                                setJoinLobbyModalOpen={setJoinLobbyModalOpen}
                                navigate={navigate}
                            />
                            <SinglePlayerWidget
                                isMobile={true}    
                            />
                        </>
                    ) : tab === "stats" ? (
                        <>
                            <MyStatsWidget
                                userId={user.id}
                                isMobile={true}
                            />
                            <TopFiveLeaderboardsWidget
                                userId={user.id}
                                isMobile={true}
                            />
                        </>
                    ) : (
                        <>
                            <GameModesWidget isMobile={true} />
                        </>
                    )
                }
            </div>
            <Modal isOpen={isJoinLobbyModalOpen} onClose={() => setJoinLobbyModalOpen(false)}><JoinLobbyModal isMobile={true} /></Modal>
        </div>
    )
}