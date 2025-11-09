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

                console.log(existingData);

                if (!existingData || !existingData.pages[0]) {
                    return {
                        // first page
                        pages: [[newMessage.chatMessage]],
                        pageParams: [0]
                    }
                }

                const lastPageIndex = existingData.pages.length - 1;
                const lastPage = existingData.pages[lastPageIndex];

                if (lastPage && lastPage?.length >= PAGE_SIZE) {
                    // Create a new page and add the message to it
                    return {
                        ...existingData,
                        pages: [...existingData.pages, [newMessage.chatMessage]],
                        pageParams: [...existingData.pageParams, existingData.pageParams.length]
                    };
                } else {
                    return {
                        ...existingData,
                        pages: [
                            ...existingData.pages.slice(0, lastPageIndex),
                            [...lastPage, newMessage.chatMessage],
                        ],
                    };
                }
            });
            break;
    }
}