import type { LobbyStatus } from "@/types/enum/LobbyStatus"

export type RevertInGameStatusResponseDto = {
    lobbyId: number,
    userId: number,
    lobbyStatus: LobbyStatus
}