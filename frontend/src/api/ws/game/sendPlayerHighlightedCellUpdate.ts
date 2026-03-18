export function sendPlayerHighlightedCellUpdate(
    send: (dest: string, body: any) => void,
    gameId: number, 
    userId: number, 
    row: number, 
    col: number
) {
    send(`/app/game/${gameId}`, {userId: userId, row: row, col: col});
}