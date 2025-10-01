import SockJS from "sockjs-client";

export function initWebSocket(): WebSocket {
        // Create a SockJS socket pointing to the server endpoint
    return new SockJS("http://localhost:8080/ws");
}