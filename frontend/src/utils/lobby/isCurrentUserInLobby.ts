import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import type { UserDto } from "@/types/dto/entity/UserDto";

export function isCurrentUserInLobby(
    lobby: LobbyDto,
    currentUser: UserDto
): boolean {
    return lobby.lobbyPlayers.some(
        (player) => player.user.id === currentUser.id
    );
}