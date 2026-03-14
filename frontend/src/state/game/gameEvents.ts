import type { CellCoordinates } from "@/types/game/GameTypes"

export type GameEvent = 
    | {
        type: "GAME_PLAYER_UPDATE"
      }
    | {
        type: "BOARD_PROGRESS_UPDATE"
      }
    | {
        type: "GAME_STATUS_UPDATE"
      }
    | {
        type: "GAME_TIMER_UPDATE"
      }
    | {
        type: "HIGHLIGHTED_CELL_UPDATE",
        playerId: number,
        coordinates: CellCoordinates
      };