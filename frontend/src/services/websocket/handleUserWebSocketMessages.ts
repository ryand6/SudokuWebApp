import { userCacheDispatcher } from "@/state/user/userCacheDispatcher";
import { QueryClient } from "@tanstack/react-query";

export function handleUserWebSocketMessages(message: any, queryClient: QueryClient) {
    switch (message.type) {
        // Updates React Query currentUser cache if the user is updated in the backend
        case "USER_UPDATED": {
            userCacheDispatcher(queryClient, {
                type: "USER_UPDATED",
                userData: message.payload
            })
            break;
        }
        case "USER_SETTINGS_UPDATED": {
            userCacheDispatcher(queryClient, {
                type: "USER_SETTINGS_UPDATED",
                field: message.payload.field,
                value: message.payload.value
            })
            break;
        }
        case "USER_SETTINGS_FIELD_REJECTED": {
            userCacheDispatcher(queryClient, {
                type: "USER_SETTINGS_FIELD_REJECTED",
                field: message.payload.field,
                value: message.payload.value
            })
            break;
        }
        case "USER_SETTINGS_VALUE_REJECTED": {
            userCacheDispatcher(queryClient, {
                type: "USER_SETTINGS_VALUE_REJECTED",
                field: message.payload.field,
                value: message.payload.value
            })
            break;
        }
    }
}