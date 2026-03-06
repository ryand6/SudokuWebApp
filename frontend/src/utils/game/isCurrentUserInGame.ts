import type { UserDto } from "@/types/dto/entity/user/UserDto";
import type { PublicGameState } from "@/types/game/GameTypes";

export function isCurrentUserInGame(
    game: PublicGameState,
    currentUser: UserDto
): boolean {
    return game.playerIds.some((id) => id === currentUser.id);
}