import { Client, ReconnectionTimeMode, type IFrame } from "@stomp/stompjs";
import { initWebSocket } from "@/services/websocket/initWebSocket";
import { MAX_RECONNECT_DELAY } from "@/context/WebSocketProvider";
import { getCsrfToken } from "@/api/rest/csrf/query/getCsrfToken";


// Factory for creating StompJS client with callback functions to handle OnStompError and onConnect
export function stompClientFactory(
    initialReconnectDelay: number,
    handleStompError: (frame: IFrame) => void, 
    handleConnect: () => void,
    handleDisconnect: () => void,
    handleWebSocketClose: () => void,
    handleWebSocketError: (event: any) => void
): Client {
    const client = new Client({
        // Tells STOMP to use a new SockJS instance
        webSocketFactory: () => initWebSocket(),
        // Auto-reconnect starts at 500ms (plus jitter to hedge against thundering herd problem)
        reconnectDelay: initialReconnectDelay,
        // Each time reconnect occurs without successful connection, double the reconnectDelay
        reconnectTimeMode: ReconnectionTimeMode.EXPONENTIAL,
        maxReconnectDelay: MAX_RECONNECT_DELAY,
        // Send heartbeat to backend every 10 secs to confirm connection is alive
        heartbeatOutgoing: 10000,
        // Expect to receive heartbeat from backend every 10 secs
        heartbeatIncoming: 10000,
        beforeConnect: async () => {
            try {
                const csrfTokenData = await getCsrfToken();
                if (csrfTokenData === null) {
                    sessionStorage.setItem("postLoginPath", window.location.pathname + window.location.search + window.location.hash);
                    window.location.replace("http://localhost:8080/login");
                    return;
                }
                client.connectHeaders = {
                    [csrfTokenData.headerName]: csrfTokenData.token
                }
            } catch (error) {
                console.error("Failed to fetch CSRF token during reconnect", error);
                client.deactivate();
            }
        },
        // Called if the server sends a STOMP error
        onStompError: handleStompError,
        onConnect: handleConnect,
        onDisconnect: handleDisconnect,
        onWebSocketClose: handleWebSocketClose,
        onWebSocketError: handleWebSocketError
    });

    return client;
}