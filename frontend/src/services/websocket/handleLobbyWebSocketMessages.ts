import { PAGE_SIZE } from "@/hooks/lobby/useGetLobbyChatMessages";
import type { GameDto } from "@/types/dto/entity/GameDtoRaw";
import type { LobbyChatMessageDto } from "@/types/dto/entity/LobbyChatMessageDto";
import { QueryClient } from "@tanstack/react-query";
import { type NavigateFunction } from "react-router-dom";

export function handleLobbyWebSocketMessages(message: any, queryClient: QueryClient, lobbyId: number, navigate: NavigateFunction) {
    switch (message.type) {
        // Updates React Query lobby cache if the lobby is updated in the backend
        case "LOBBY_UPDATED":
            queryClient.setQueryData(["lobby", lobbyId], message.payload);
            break;
        // Transport lobby players to game page when game has started
        case "GAME_CREATED":
            const gameDto: GameDto = message.payload;
            queryClient.setQueryData(["game", gameDto.id], gameDto);
            navigate(`/game/${gameDto.id}`);
            break;
        // Updates session storage if message is received in lobby chat
        case "LOBBY_CHAT_MESSAGE":
            const newMessage: {chatMessage: LobbyChatMessageDto} = {chatMessage: message.chatMessage};
            queryClient.setQueryData<LobbyChatMessageDto[]>(["lobbyChat", lobbyId], (existingData: any) => {
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