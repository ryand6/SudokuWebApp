export function sendPlayerHighlightedCellUpdate(
    send: (dest: string, body: any) => void,
    gameId: number,
    row: number, 
    col: number
) {
    send(`/app/game/${gameId}/update-highlighted-cell`, {row: row, col: col});
}