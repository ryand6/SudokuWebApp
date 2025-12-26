import type { Difficulty } from "@/types/enum/Difficulty"

export type UpdateLobbyDifficultyDto = {
    lobbyId: number,
    difficulty: Difficulty
}