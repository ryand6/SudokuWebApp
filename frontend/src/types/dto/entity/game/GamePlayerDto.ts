import type { PlayerColour } from "@/types/enum/PlayerColour";
import type { GameResult } from "@/types/enum/GameResult";
import type { UserDto } from "../user/UserDto";
import type { CellStatus } from "@/types/enum/CellStatus";

export type GamePlayerDto = {
    user: UserDto,
    playerColour: PlayerColour,
    boardProgress: CellStatus[],
    score: number,
    firsts: number,
    mistakes: number,
    maxStreak: number,
    gameLoaded: boolean,
    gameResult: GameResult
}