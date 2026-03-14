export const queryKeys = {
    user: ["currentUser"] as const,
    userRank: ["userRank"] as const,
    userTokens: (userId: number) => ["user", userId, "tokens"] as const,
    topFivePlayers: ["topFivePlayers"] as const,
    publicLobbies: ["publicLobbiesList"] as const,
    lobby: (lobbyId: number) => ["lobby", lobbyId] as const,
    lobbyChat: (lobbyId: number) => ["lobbyChat", lobbyId] as const,
    game: (gameId: number) => ["game", gameId] as const,
    gamePlayerState: (gameId: number, userId: number | undefined) => ["game", gameId, "user", userId] as const,
}