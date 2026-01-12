export function sendLobbyChatMessage(send: (dest: string, body: any) => void, lobbyId: number, userId: number, message: string) {

    message = message.replace(/[\n]/g,'\\n');

    console.log("CHAT MESSAGE CONTENTS: ", message);

    send(`/app/lobby/${lobbyId}/chat`, {userId: userId, message: message});
} 