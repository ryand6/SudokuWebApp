import type { Difficulty } from "@/types/enum/Difficulty"

export type UpdateLobbyDifficultyDto = {
    lobbyId: number,
    userId: number,
    difficulty: Difficulty
}