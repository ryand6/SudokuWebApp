import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { LobbyChatMessageDto } from "@/types/dto/entity/lobby/LobbyChatMessageDto";
import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";

export type LobbyEvent = 
    | {
        type: "LOBBY_UPDATED",
        lobbyData: LobbyDto
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