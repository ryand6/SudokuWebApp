import type { UserDto } from "@/types/dto/entity/UserDto";
import type { GameState } from "@/types/game/GameTypes";

export function isCurrentUserInGame(
    game: GameState,
    currentUser: UserDto
): boolean {
    return game.playerIds.some((id) => id === currentUser.id);
}