import type { GameDto } from "@/types/dto/entity/GameDto";
import type { UserDto } from "@/types/dto/entity/UserDto";

export function isCurrentUserInGame(
    game: GameDto,
    currentUser: UserDto
): boolean {
    return game.gameStates.some(
        (player) => player.user.id === currentUser.id
    );
}