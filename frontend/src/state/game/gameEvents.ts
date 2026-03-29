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
        userId: number,
        coordinates: CellCoordinates
      };

export type GameEventLog = 
    | {
        type: "GAME_EVENT"
      }

export type GamePlayerStateEvent = 
    | {
        type: "CELL_UPDATE_SUBMITTED",
        row: number,
        col: number,
        value: number
      }
    | {
        type: "CELL_UPDATE_ACCEPTED",
        row: number,
        col: number,
        value: number,
        currentStreak: number
      }
    | {
        type: "CELL_UPDATE_REJECTED",
        row: number,
        col: number,
        value: number,
        currentStreak: number
      }
    | {
        type: "CELL_UPDATE_INVALID",
        row: number,
        col: number,
        value: number
      }
    | {
        type: "NOTE_UPDATE"
      }
    | {
        type: "SETTINGS_UPDATED"
      };