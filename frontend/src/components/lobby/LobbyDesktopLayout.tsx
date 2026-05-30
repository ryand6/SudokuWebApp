import { getEpochTimeFromTimestamp } from "@/utils/time/getEpochTimeFromTimestamp";
import { TimerCountdown } from "../ui/custom/TimerCountdown";
import { Button } from "../ui/button";
import { LobbyPlayersPanel } from "./LobbyPlayersPanel";
import { LobbySettingsPanel } from "./LobbySettingsPanel";
import { LobbyChatPanel } from "./LobbyChatPanel";
import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";
import type { UserDto } from "@/types/dto/entity/user/UserDto";

export function LobbyDesktopLayout({
    lobby,
    currentUser,
    handleLeaveLobbyClick
}: {
    lobby: LobbyDto,
    currentUser: UserDto,
    handleLeaveLobbyClick: () => void
}) {
    return (
        <div id="lobby-container" className="flex flex-col flex-1">
            <div id="lobby-header" className="flex flex-row justify-between">
                <h1 className="text-foreground-strong font-bold text-shadow m-3">{lobby?.lobbyName}</h1>
                {lobby.lobbyCountdown.countdownActive && lobby.lobbyCountdown.countdownEndsAt && (
                    <TimerCountdown endTime={getEpochTimeFromTimestamp(lobby.lobbyCountdown.countdownEndsAt)} />
                )}
                <Button className="m-2 cursor-pointer" onClick={handleLeaveLobbyClick} variant={"destructive"}>Leave Lobby</Button>
            </div>
            <div id="lobby-content" className="flex flex-col flex-1 gap-4 max-h-[75vh]">
                <div className="flex flex-row flex-1 min-h-0">
                    {/* Desktop only: split left/right columns */}
                    <div className="flex flex-1 min-h-0">
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
                            <LobbyChatPanel lobbyId={lobby.id} userId={currentUser.id} />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}
