import { QueryClient } from "@tanstack/react-query";

export function handleLobbyWebSocketMessages(message: any, queryClient: QueryClient, lobbyId: number) {
    switch (message.type) {
        // Updates React Query currentUser cache if the user is updated in the backend
        case "LOBBY_UPDATED":
            queryClient.setQueryData(["lobby", lobbyId], message.payload);
    }
}