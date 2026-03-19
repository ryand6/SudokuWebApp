export function sendLobbyChatMessage(
    send: (dest: string, body: any) => void, 
    lobbyId: number, 
    userId: number, 
    message: string
) {
    send(`/app/lobby/${lobbyId}/chat/send-message`, {userId: userId, message: message});
} 