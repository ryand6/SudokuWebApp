import type { Difficulty } from "@/types/enum/Difficulty"

export type SudokuPuzzleDto = {
    id: number, 
    initialBoardState: string,
    difficulty: Difficulty
}