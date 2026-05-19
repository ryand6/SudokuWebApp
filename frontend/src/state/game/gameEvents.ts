import type { GameChatMessageDto } from "@/types/dto/entity/game/GameChatMessageDto";
import type { GameEventDto } from "@/types/dto/entity/game/GameEventDto";
import type { GamePlayerDto } from "@/types/dto/entity/game/GamePlayerDto";
import type { GameResult } from "@/types/enum/GameResult";
import type { GameStatus } from "@/types/enum/GameStatus";
import type { CellCoordinates, LeaderboardResult } from "@/types/game/GameTypes"

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
        type: "HIGHLIGHTED_CELL_UPDATE",
        userId: number,
        coordinates: CellCoordinates
      }
    | {
        type: "PLAYER_FORFEIT",
        gamePlayer: GamePlayerDto
      }
    | {
        type: "PLAYER_FINISHED",
        gamePlayer: GamePlayerDto
      }
    | {
        type: "GAME_ENDED_PREMATURELY"
      }
    | {
        type: "GAME_RESULTS_DETERMINED",
        gameResults: Record<number, GameResult>
      }
    | {
        type: "GAME_STATUS_UPDATE",
        gameStatus: GameStatus
      }
    | {
        type: "GAME_END_TIMER_UPDATE",
        gameEndsAt: string
      }
    | {
        type: "GAME_CLOCKS_INITIALISED",
        gameStartsAt: string,
        gameEndsAt: string,
        gameStatus: GameStatus
      };

export type GameEventLog = 
    | {
        type: "GAME_EVENT",
        newMessage: GameEventDto
      }

export type GameChatEvent = 
    | {
      type: "GAME_CHAT_MESSAGE",
      newMessage: GameChatMessageDto
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
        type: "LEADERBOARD_SCORE_CALCULATED",
        leaderboardResult: LeaderboardResult
      };