import { QueryClient } from "@tanstack/react-query";
import { addLobbyMessage } from "../lobby/lobbyMessagesService";

export function handleLobbyWebSocketMessages(message: any, queryClient: QueryClient, lobbyId: number) {
    switch (message.type) {
        // Updates React Query lobby cache if the lobby is updated in the backend
        case "LOBBY_UPDATED":
            queryClient.setQueryData(["lobby", lobbyId], message.payload);
            break;
        // Updates session storage if message is received in lobby chat
        case "LOBBY_CHAT_MESSAGE":
            queryClient.setQueryData<{user: string, message: string}[]>(["lobbyChat", lobbyId], () => {
                const updatedMessages = addLobbyMessage(lobbyId, message.username, message.payload);
                return updatedMessages;
            });
            break;
    }
}