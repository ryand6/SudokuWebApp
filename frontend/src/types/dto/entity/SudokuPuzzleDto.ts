import type { Difficulty } from '../../enum/Difficulty';

export type SudokuPuzzleDto = {
    id: number, 
    initialBoardState: string,
    difficulty: Difficulty
}