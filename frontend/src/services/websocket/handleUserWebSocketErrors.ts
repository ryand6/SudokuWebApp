import { toast } from "react-toastify";

export function handleUserWebSocketErrors(message: any) {
    switch (message.type) {
        case "CHAT_ERROR":
            toast.error(message.payload);
            break;
        case "LOBBY_ERROR":
            toast.error(message.payload);
            break;
        case "GENERAL_ERROR":
            toast.error(message.payload);
            break;
    }
}