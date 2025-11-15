import { PAGE_SIZE } from "@/hooks/lobby/useGetLobbyChatMessages";
import type { LobbyChatMessageDto } from "@/types/dto/entity/LobbyChatMessageDto";
import { QueryClient } from "@tanstack/react-query";

export function handleLobbyWebSocketMessages(message: any, queryClient: QueryClient, lobbyId: number) {
    switch (message.type) {
        // Updates React Query lobby cache if the lobby is updated in the backend
        case "LOBBY_UPDATED":
            queryClient.setQueryData(["lobby", lobbyId], message.payload);
            break;
        // Updates session storage if message is received in lobby chat
        case "LOBBY_CHAT_MESSAGE":
            const newMessage: {chatMessage: LobbyChatMessageDto} = {chatMessage: message.chatMessage};

            console.log(newMessage);

            queryClient.setQueryData<LobbyChatMessageDto[]>(["lobbyChat", lobbyId], (existingData: any) => {

                console.log(existingData.pageParams);

                // If no data exists, add first message
                if (!existingData || !existingData.pages[0]) {
                    return {
                        // first page
                        pages: [[newMessage.chatMessage]],
                        pageParams: [0]
                    }
                }

                const firstPage = existingData.pages[0];

                if (firstPage && firstPage?.length >= PAGE_SIZE) {
                    // Create a new page with message in it and insert at front of nested array 
                    return {
                        ...existingData,
                        pages: [[newMessage.chatMessage], ...existingData.pages],
                        pageParams: [...existingData.pageParams, existingData.pageParams.length]
                    };
                } else {
                    // Add message to first page as this contains the most recent messages - add to start of page as the order will be reversed on render
                    return {
                        ...existingData,
                        pages: [
                            [newMessage.chatMessage, ...firstPage],
                            ...existingData.pages.slice(1),
                        ],
                    };
                }
            });
            break;
    }
}