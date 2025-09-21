import { useQueryClient } from "@tanstack/react-query";

export function handleUserWebSocketMessages(message: any) {
    const queryClient = useQueryClient();

    switch (message.type) {
        // Updates React Query currentUser cache if the user is updated in the backend
        case "USER_UPDATED":
            queryClient.setQueryData(["currentUser"], message.payload);
    }
}