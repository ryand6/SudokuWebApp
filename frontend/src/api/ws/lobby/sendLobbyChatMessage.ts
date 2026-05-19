export function sendLobbyChatMessage(
    send: (dest: string, body: any) => void, 
    lobbyId: number,
    message: string
) {
    send(`/app/lobby/${lobbyId}/chat/send-message`, {message: message});
} 