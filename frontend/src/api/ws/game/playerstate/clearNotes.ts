export function clearNotes(
    send: (dest: string, body: any) => void,
    gameId: number, 
    userId: number, 
    row: number, 
    col: number
) {
    send(`/app/game/${gameId}/user/${userId}/clear-notes`, {row: row, col: col});
}