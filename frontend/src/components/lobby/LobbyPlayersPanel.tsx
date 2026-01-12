import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { Button } from "../ui/button";
import type { UserDto } from "@/types/dto/entity/UserDto";
import type { LobbyPlayerDto } from "@/types/dto/entity/LobbyPlayerDto";
import { toast } from "react-toastify";
import type { LobbyStatus } from "@/types/enum/LobbyStatus";
import { useUpdateLobbyPlayerStatus } from "@/hooks/lobby/useUpdateLobbyPlayerStatus";

export function LobbyPlayersPanel({lobby, currentUser}: {lobby: LobbyDto, currentUser: UserDto}) {

    const currentLobbyPlayer: LobbyPlayerDto | undefined = lobby.lobbyPlayers.find((lp) => lp.id.userId === currentUser.id);

    // Custom hook used to update lobby player status in backend and frontend cache
    const updateLobbyPlayerStatus = useUpdateLobbyPlayerStatus();

    // Create an array of undefined values the size of the number of player slots left to fill - used to indicate in UI how many player slots are left
    const playerSlotsRemaining = Array.from(
        { length: 4 - lobby.lobbyPlayers.length }
    );

    const handleClick = () => {
        if (!currentLobbyPlayer) {
            toast.error("Lobby player does not exist");
            return;
        }
        const updatedStatus: LobbyStatus = currentLobbyPlayer.lobbyStatus === "READY" ? "WAITING" : "READY";
        updateLobbyPlayerStatus.mutate({ lobbyId: lobby.id, userId: currentUser.id, lobbyStatus: updatedStatus });
    };

    return (
        <div id="lobby-player-panel" className="flex flex-col flex-1 lobby-card">
            <h2 className="card-header">Players ({lobby.lobbyPlayers.length}/4)</h2>
            {lobby.lobbyPlayers.sort((lobbyPlayerA, lobbyPlayerB) => lobbyPlayerA.user.username.localeCompare(lobbyPlayerB.user.username)).map((player, index) => {
                return (
                    <div id="player-row" key={index}>
                        {player.user.id === lobby.host.id && <span id="host-star">★</span>}
                        <span id="player-name">{player.user.username}</span>
                        {player.lobbyStatus === "READY" ? <span className="bg-[#c6f6d5] text-[#22543d]">Ready</span> : player.lobbyStatus === "INGAME" ? 
                        <span className="bg-[#bee3f8] text-[#2a69ac]">In Game</span> : <span className="bg-[#fed7d7] text-[#742a2a]">Waiting</span>}
                    </div>
                )
            })}
            {!lobby.settingsLocked && playerSlotsRemaining.map((_, index) => {
                return (
                    <div id="empty-player-row" key={index}>
                        <span>Waiting for player...</span>
                    </div>
                )
            })}
            <Button onClick={handleClick} disabled={lobby.lobbyPlayers.length < 2}>Toggle Ready</Button>
        </div>
    )
}