import SockJS from "sockjs-client";
import { apiUrl } from "@/api/apiFetch";

// Local development connects straight to the local backend.
// A deployed build uses the backend address from VITE_API_BASE_URL.
const WS_URL: string = import.meta.env.PROD
    ? apiUrl("/ws")
    : "http://localhost:8080/ws";

export function initWebSocket(): WebSocket {
        // Create a SockJS socket pointing to the server endpoint
    return new SockJS(WS_URL);
}