import { QueryClient } from "@tanstack/react-query";
import { updateLobbyMessages } from "../lobby/lobbyMessagesService";

export function handleLobbyWebSocketMessages(message: any, queryClient: QueryClient, lobbyId: number) {
    switch (message.type) {
        // Updates React Query lobby cache if the lobby is updated in the backend
        case "LOBBY_UPDATED":
            queryClient.setQueryData(["lobby", lobbyId], message.payload);
            break;
        // Updates session storage if message is received in lobby chat
        case "LOBBY_CHAT_MESSAGE":
            const newMessage: {username: string, message: string} = {username: message.username, message: message.payload};
            queryClient.setQueryData<{username: string, message: string}[]>(["lobbyChat", lobbyId], (existingMessages = []) => {
                let updatedMessages = [...existingMessages, newMessage].slice(-10);
                updateLobbyMessages(lobbyId, updatedMessages);
                return updatedMessages;
            });
            break;
    }
}