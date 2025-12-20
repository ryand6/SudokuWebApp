import { toast } from "react-toastify";

export function handleUserWebSocketErrors(message: any) {
    switch (message.type) {
        // Updates React Query currentUser cache if the user is updated in the backend
        case "CHAT_ERROR":
            toast.error(message.payload);
    }
}