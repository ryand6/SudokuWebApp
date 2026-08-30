import { useGetCurrentUser } from "../api/rest/users/query/useGetCurrentUser";
import { useNavigate } from "react-router-dom";
import { useIsMobile } from "@/hooks/global/useIsMobile";
import { DashboardMobileLayout } from "@/components/dashboard/layouts/DashboardMobileLayout";
import { DashboardDesktopLayout } from "@/components/dashboard/layouts/DashboardDesktopLayout";
import { useGetActiveLobby } from "@/api/rest/users/query/useGetActiveLobby";
import { useLeaveGame } from "@/api/rest/game/mutate/useLeaveGame";
import { useLeaveLobby } from "@/api/rest/lobby/mutate/useLeaveLobby";
import { useState } from "react";

export function DashboardPage() {

    const { data: currentUser } = useGetCurrentUser();
    const { data: userActiveLobby } = useGetActiveLobby();

    const [isModalOpen, setModalOpen] = useState(false);
    const [isLeaveLobbyAlertOpen, setIsLeaveLobbyAlertOpen] = useState(false);
    const [isLeaveGameAlertOpen, setIsLeaveGameAlertOpen] = useState(false);

    const onRejoinClick = () => {
        if (!userActiveLobby) return;
        const path = userActiveLobby.inGame ? `/game/${userActiveLobby.currentGameId}` : `/lobby/${userActiveLobby.id}`;
        navigate(path);
    }

    const leaveLobbyHandler = useLeaveLobby();
    const leaveGameHandler = useLeaveGame();

    console.log("userActiveLobby: ", userActiveLobby);

    const navigate = useNavigate();

    const isMobile = useIsMobile();

    if (!currentUser) return;

    return (
        isMobile ?             
            <DashboardMobileLayout 
                user={currentUser} 
                activeLobby={userActiveLobby}
                leaveLobbyHandler={leaveLobbyHandler}
                leaveGameHandler={leaveGameHandler}
                isModalOpen={isModalOpen}
                setModalOpen={setModalOpen}
                isLeaveLobbyAlertOpen={isLeaveLobbyAlertOpen}
                setIsLeaveLobbyAlertOpen={setIsLeaveLobbyAlertOpen}
                isLeaveGameAlertOpen={isLeaveGameAlertOpen}
                setIsLeaveGameAlertOpen={setIsLeaveGameAlertOpen}
                onRejoinClick={onRejoinClick}
                navigate={navigate}
            />
        :
            <DashboardDesktopLayout 
                user={currentUser} 
                activeLobby={userActiveLobby}
                leaveLobbyHandler={leaveLobbyHandler}
                leaveGameHandler={leaveGameHandler}
                isModalOpen={isModalOpen}
                setModalOpen={setModalOpen}
                isLeaveLobbyAlertOpen={isLeaveLobbyAlertOpen}
                setIsLeaveLobbyAlertOpen={setIsLeaveLobbyAlertOpen}
                isLeaveGameAlertOpen={isLeaveGameAlertOpen}
                setIsLeaveGameAlertOpen={setIsLeaveGameAlertOpen}
                onRejoinClick={onRejoinClick}
                navigate={navigate}
            /> 
                
    )
}