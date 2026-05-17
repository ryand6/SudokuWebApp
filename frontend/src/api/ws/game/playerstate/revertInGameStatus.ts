export function revertInGameStatus(
    send: (dest: string, body: any) => void,
    gameId: number,
    userId: number,
    lobbyId: number
) {
    send(`/app/game/${gameId}/user/${userId}/revert-in-game-status`, {lobbyId: lobbyId});
}