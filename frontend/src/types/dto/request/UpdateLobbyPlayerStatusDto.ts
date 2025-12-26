import type { LobbyStatus } from "@/types/enum/LobbyStatus"

export type UpdateLobbyPlayerStatusDto = {
    lobbyId: number, 
    userId: number,
    lobbyStatus: LobbyStatus
}