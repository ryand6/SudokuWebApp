export function confirmPlayerGameLoaded(
    send: (dest: string, body: any) => void,
    gameId: number
) {
    send(`/app/game/${gameId}/game-loaded-confirmation`, {});
}