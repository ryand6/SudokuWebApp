import type { GameEventDto } from "@/types/dto/entity/game/GameEventDto";
import type { CellCoordinates } from "@/types/game/GameTypes"

export type GameEvent = 
    | {
        type: "PLAYER_CELL_UPDATE_ACCEPTED",
        userId: number,
        score: number,
        firsts: number,
        maxStreak: number,
        gameEndsAt: string,
        row: number,
        col: number,
        firstUserId: number | null
      }
    | {
        type: "PLAYER_CELL_UPDATE_REJECTED",
        userId: number,
        score: number,
        mistakes: number,
        gameEndsAt: string
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
        type: "GAME_EVENT",
        newMessage: GameEventDto
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
        type: "STREAK_RESET"
      }
    | {
        type: "NOTE_UPDATE",
        row: number,
        col: number,
        note: number
      }
    | {
        type: "CELL_CLEAR",
        row: number,
        col: number
      }
    | {
        type: "SETTINGS_UPDATED"
      };