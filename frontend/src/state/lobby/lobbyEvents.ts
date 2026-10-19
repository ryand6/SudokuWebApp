import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { LobbyChatMessageDto } from "@/types/dto/entity/lobby/LobbyChatMessageDto";
import type { LobbyPlayerDto } from "@/types/dto/entity/lobby/LobbyPlayerDto";
import type { Difficulty } from "@/types/enum/Difficulty";
import type { LobbyStatus } from "@/types/enum/LobbyStatus";
import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset";

export type LobbyEvent = 
    | {
        type: "LOBBY_DIFFICULTY_UPDATED",
        lobbyId: number,
        difficulty: Difficulty
      }
    | {
        type: "LOBBY_TIME_LIMIT_UPDATED",
        lobbyId: number,
        timeLimit: TimeLimitPreset
      }
    | {
        type: "LOBBY_PLAYER_STATUS_UPDATED",
        lobbyId: number,
        userId: number,
        lobbyStatus: LobbyStatus
      }
    | {
        type: "LOBBY_PLAYER_JOINED",
        lobbyId: number,
        lobbyPlayer: LobbyPlayerDto
      }
    | {
        type: "LOBBY_PLAYER_LEFT",
        lobbyId: number,
        userId: number
      }
    | {
        type: "LOBBY_GAME_CREATED",
        lobbyId: number,
        gameId: number,
        inGame: boolean
      }
    | {
        type: "GAME_ENDED",
        lobbyId: number
      };

export type GameCreationEvent = 
    | {
        type: "GAME_CREATED",
        gameData: GameDto
    };


export type LobbyChatEvent =
    | {
        type: "LOBBY_CHAT_MESSAGE",
        newMessage: LobbyChatMessageDto
      };    