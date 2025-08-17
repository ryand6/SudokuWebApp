import type { SudokuPuzzleDto } from './SudokuPuzzleDto.ts';
import type { GameStateDto } from './GameStateDto.ts';
import type { LobbyDto } from './LobbyDto.ts';


export type GameDto = {
    id: number,
    lobby: LobbyDto,
    sudokuPuzzle: SudokuPuzzleDto,
    gameStates: GameStateDto[]
}