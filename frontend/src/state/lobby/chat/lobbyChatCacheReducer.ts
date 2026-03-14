import type { InfiniteData } from "@tanstack/react-query";
import type { LobbyChatEvent } from "../lobbyEvents";
import type { LobbyChatMessageDto } from "@/types/dto/entity/lobby/LobbyChatMessageDto";
import { PAGE_SIZE } from "@/api/rest/lobbychat/query/useGetLobbyChatMessages";

export function lobbyChatCacheReducer(
    existingData: InfiniteData<LobbyChatMessageDto[]> | undefined,
    event: LobbyChatEvent
) {
    switch (event.type) {
        case "LOBBY_CHAT_MESSAGE": {
            // If no data exists, add first message
            if (!existingData || !existingData.pages[0]) {
                return {
                    // first page
                    pages: [[event.newMessage]],
                    pageParams: [0]
                }
            }

            const firstPage = existingData.pages[0];

            if (firstPage && firstPage?.length >= PAGE_SIZE) {
                // Create a new page with message in it and insert at front of nested array 
                return {
                    ...existingData,
                    pages: [[event.newMessage], ...existingData.pages],
                    pageParams: [...existingData.pageParams, existingData.pageParams.length]
                };
            } else {
                // Add message to first page as this contains the most recent messages - add to start of page as the order will be reversed on render
                return {
                    ...existingData,
                    pages: [
                        [event.newMessage, ...firstPage],
                        ...existingData.pages.slice(1),
                    ],
                };
            }
        }
        default:
            return existingData;
    }
}