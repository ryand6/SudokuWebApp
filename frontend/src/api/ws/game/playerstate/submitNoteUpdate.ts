export function submitNoteUpdate(
    send: (dest: string, body: any) => void,
    gameId: number, 
    userId: number, 
    row: number, 
    col: number,
    note: number
) {
    send(`/app/game/${gameId}/user/${userId}/submit-note-update`, {row: row, col: col, note: note});
}