import { useState } from "react";
import { Button } from "../ui/button";
import { TimerCountdown } from "../ui/custom/TimerCountdown";
import { getEpochTimeFromTimestamp } from "@/utils/time/getEpochTimeFromTimestamp";
import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";
import { LobbyPlayersPanel } from "./LobbyPlayersPanel";
import { LobbySettingsPanel } from "./LobbySettingsPanel";
import { LobbyChatPanel } from "./LobbyChatPanel";
import type { UserDto } from "@/types/dto/entity/user/UserDto";

export function LobbyMobileLayout({
    lobby,
    currentUser,
    handleLeaveLobbyClick
}: {
    lobby: LobbyDto,
    currentUser: UserDto,
    handleLeaveLobbyClick: () => void
}) {

    const [activePanel, setActivePanel] = useState<"players" | "settings" | "chat">("players");

    return (
        <div id="lobby-container" className="flex flex-col flex-1">
            <div id="lobby-header" className="flex flex-row justify-between">
                <h1 className="text-foreground-strong font-bold text-shadow m-3">{lobby?.lobbyName}</h1>
                {lobby.lobbyCountdown.countdownActive && lobby.lobbyCountdown.countdownEndsAt && (
                    <TimerCountdown endTime={getEpochTimeFromTimestamp(lobby.lobbyCountdown.countdownEndsAt)} />
                )}
                <Button className="m-2 cursor-pointer" onClick={handleLeaveLobbyClick} variant={"destructive"}>Leave Lobby</Button>
            </div>
            <div id="lobby-content" className="flex flex-col flex-1 gap-4 max-h-[70vh]">
                <div id="mobile-tabs">
                    <Button 
                        onClick={() => setActivePanel("players")}
                        className="cursor-pointer"
                    >
                        Players
                    </Button>
                    <Button 
                        onClick={() => setActivePanel("settings")}
                        className="cursor-pointer"
                    >
                        Settings
                    </Button>
                    <Button 
                        onClick={() => setActivePanel("chat")}
                        className="cursor-pointer"
                    >
                        Lobby Chat
                    </Button>
                </div>
                <div className="flex flex-row flex-1 min-h-0">
                    {/* Mobile only: render the active panel directly */}
                    <div className="flex-1 flex flex-col min-h-0 max-w-[95%] m-5">
                        {activePanel === "players" && <LobbyPlayersPanel lobby={lobby} currentUser={currentUser} />}
                        {activePanel === "settings" && <LobbySettingsPanel lobby={lobby} currentUser={currentUser} />}
                        {activePanel === "chat" && <LobbyChatPanel lobbyId={lobby.id} userId={currentUser.id} />}
                    </div>
                </div>
            </div>
        </div>
    )
}