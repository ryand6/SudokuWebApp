import type { LobbyDto } from "@/types/dto/entity/LobbyDto";

export function LobbyPlayersPanel({lobby}: {lobby: LobbyDto}) {
    return (
        <div id="lobby-player-panel">
            <h2>Players ({lobby.lobbyPlayers.length}/4)</h2>
            {lobby.lobbyPlayers.map((player) => {
                return (
                    <div id="player-row">
                        {player.user.id === lobby.hostId && <span id="host-star">★</span>}
                        <span id="player-name">{player.user.username}</span>
                        {player.lobbyStatus === "READY" ? <span className="bg-[#c6f6d5] text-[#22543d]"></span> : player.lobbyStatus === "INGAME" ? 
                        <span className="bg-[#bee3f8] text-[#2a69ac]">In Game</span> : <span className="bg-[#fed7d7] text-[#742a2a]">Waiting</span>}
                    </div>
                )
            })}
        </div>
    )
}