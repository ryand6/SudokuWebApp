import type { SudokuPuzzleDto } from './SudokuPuzzleDto.ts';
import type { GameStateDtoRaw } from './GameStateDtoRaw.ts';
import type { LobbyDto } from './LobbyDto.ts';


export type GameDtoRaw = {
    id: number,
    lobby: LobbyDto,
    sudokuPuzzle: SudokuPuzzleDto,
    gameStates: GameStateDtoRaw[]
}