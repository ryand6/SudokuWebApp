import type { GameMode } from "@/types/enum/GameMode";

export const queryKeys = {
    user: ["currentUser"] as const,
    userTokens: (userId: number) => ["user", userId, "tokens"] as const,
    topTenWithUserRank: (gameMode: GameMode) => ["topTenWithUserRank", gameMode] as const,
    publicLobbies: ["publicLobbiesList"] as const,
    userActiveLobby: ["userActiveLobby"] as const,
    lobby: (lobbyId: number) => ["lobby", lobbyId] as const,
    lobbyChat: (lobbyId: number) => ["lobbyChat", lobbyId] as const,
    game: (gameId: number) => ["game", gameId] as const,
    gameChat: (gameId: number) => ["gameChat", gameId] as const,
    gameEvents: (gameId: number) => ["gameEvents", gameId] as const,
    gamePlayerState: (gameId: number, userId: number | undefined) => ["game", gameId, "user", userId] as const,
}