import type { PlayerColour } from "@/types/enum/PlayerColour";
import type { GameResult } from "@/types/enum/GameResult";
import type { UserDto } from "../user/UserDto";

export type GamePlayerDto = {
    user: UserDto,
    playerColour: PlayerColour,
    boardProgress: boolean[],
    score: number,
    firsts: number,
    mistakes: number,
    maxStreak: number,
    gameLoaded: boolean,
    gameResult: GameResult
}