import type { QueryClient } from "@tanstack/react-query";

export function handleGamePlayerStateWebSocketMessages(message: any, queryClient: QueryClient, gameId: number, userId: number) {
    switch (message.type) {
        case "CELL_UPDATE_ACCEPTED": {
            // IMPLEMENT - INCLUDE MULTIPLIER AND STREAK DETAILS
            break;
        }
        case "CELL_UPDATE_REJECTED": {
            // IMPLEMENT - INCLUDE MULTIPLIER AND STREAK DETAILS
            break;
        }
        case "NOTE_UPDATE_ACCEPTED": {
            // IMPLEMENT
            break;
        }
        case "NOTE_UPDATE_REJECTED": {
            // IMPLEMENT
            break;
        }
        case "SETTINGS_UPDATED": {
            // IMPLEMENT
            break;
        }
    }
}