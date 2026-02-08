import type { GameStateDto } from "./GameStateDto"
import type { LobbyDto } from "./LobbyDto"
import type { SudokuPuzzleDto } from "./SudokuPuzzleDto"

export type GameDto = {
    id: number,
    lobby: LobbyDto,
    sudokuPuzzle: SudokuPuzzleDto,
    gameStates: GameStateDto[]
}