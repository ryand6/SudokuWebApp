export function submitCellUpdate(
    send: (dest: string, body: any) => void,
    gameId: number, 
    userId: number, 
    row: number, 
    col: number,
    value: number
) {
    send(`/app/game/${gameId}/user/${userId}/submit-cell-update`, {row: row, col: col, value: value});
}