export function sendLobbyChatMessage(send: (dest: string, body: any) => void, lobbyId: number, userId: number, message: string) {
    send(`/app/lobby/${lobbyId}/chat`, {userId: userId, message: message});
}