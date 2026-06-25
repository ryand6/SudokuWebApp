import type { LobbyPlayerDto } from "@/types/dto/entity/lobby/LobbyPlayerDto";

export function isCurrentUserInLobby(
    lobbyPlayers: LobbyPlayerDto[],
    userId: number
): boolean {
    return lobbyPlayers.some(
        (player) => player.user.id === userId
    );
}