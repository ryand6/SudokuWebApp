import type { LobbyDto } from "@/types/dto/entity/LobbyDto";

export function LobbyPlayersPanel({lobby}: {lobby: LobbyDto}) {
    return (
        <div id="lobby-player-panel" className="flex flex-col lobby-card">
            <h2>Players ({lobby.lobbyPlayers.length}/4)</h2>
            {lobby.lobbyPlayers.sort((lobbyPlayerA, lobbyPlayerB) => lobbyPlayerA.user.username.localeCompare(lobbyPlayerB.user.username)).map((player, index) => {
                return (
                    <div id="player-row" key={index}>
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