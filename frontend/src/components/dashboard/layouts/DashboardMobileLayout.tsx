import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";
import type { UserDto } from "@/types/dto/entity/user/UserDto"
import type { LeaveGameRequestDto } from "@/types/dto/request/LeaveGameRequestDto";
import type { LeaveLobbyRequestDto } from "@/types/dto/request/LeaveLobbyRequestDto";
import type { LobbyDetailsDto } from "@/types/dto/response/LobbyDetailsDto";
import type { UseMutateFunction } from "@tanstack/react-query";
import { type Dispatch, type SetStateAction } from "react";
import type { NavigateFunction } from "react-router-dom";
import { OnlinePlayWidget } from "../widgets/OnlinePlayWidget";
import { MyStatsWidget } from "../widgets/MyStatsWidget";
import { TopFiveLeaderboardsWidget } from "../widgets/TopFiveLeaderboardsWidget";
import { SinglePlayerWidget } from "../widgets/SinglePlayerWidget";
import { GameModesWidget } from "../widgets/GameModesWidget";

export function DashboardMobileLayout({
    user,
    activeLobby,
    leaveLobbyHandler,
    leaveGameHandler,
    isModalOpen,
    setModalOpen,
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
    isModalOpen: boolean,
    setModalOpen: Dispatch<SetStateAction<boolean>>,
    isLeaveLobbyAlertOpen: boolean,
    setIsLeaveLobbyAlertOpen: Dispatch<SetStateAction<boolean>>,
    isLeaveGameAlertOpen: boolean,
    setIsLeaveGameAlertOpen: Dispatch<SetStateAction<boolean>>,
    onRejoinClick: () => void,   
    navigate: NavigateFunction
}) {
    const iconSize = 16;

    console.log("Active Lobby: ", activeLobby);

    return (
        <div className="flex flex-col w-full h-full font-display">
            <div className="flex items-center px-5 py-5 border-b-border border-b-2">
                <div className="tracking-wide font-semibold text-foreground text-lg">
                    Hello, {user.username}!
                </div>
            </div>
            <div className="flex flex-col items-center w-full h-full gap-4 p-5">
                <OnlinePlayWidget 
                    isMobile={false}
                    isActiveLobby={activeLobby ? true : false}
                    setModalOpen={setModalOpen}
                    navigate={navigate}
                />
                <MyStatsWidget
                    userId={user.id}
                    isMobile={false}
                />
                <TopFiveLeaderboardsWidget
                    userId={user.id}
                    isMobile={false}
                />
                <SinglePlayerWidget
                    isMobile={false}    
                />
                <GameModesWidget isMobile={true} />
            </div>
        </div>
    )
}