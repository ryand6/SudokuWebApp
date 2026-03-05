import type { PlayerColour } from "@/types/enum/PlayerColour";
import type { UserDto } from "../user/UserDto";
import type { GameResult } from "@/types/enum/GameResult";
import type { GamePlayerStateDtoRaw } from "./GamePlayerStateDtoRaw";
import type { GamePlayerSettingsDto } from "./GamePlayerSettingsDto";

export type GamePlayerDto = {
    user: UserDto,
    gamePlayerState: GamePlayerStateDtoRaw,
    gamePlayerSettings: GamePlayerSettingsDto,
    playerColour: PlayerColour,
    score: number,
    gameLoaded: boolean,
    gameResult: GameResult
}