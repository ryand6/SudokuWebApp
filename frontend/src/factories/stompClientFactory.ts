import { Client, type IFrame } from "@stomp/stompjs";
import type { CsrfResponseData } from "../types/dto/auth/CsrfResponseData";


// Factory for creating StompJS client with callback functions to handle OnStompError and onConnect
export function stompClientFactory(
    socket: WebSocket, 
    csrfTokenData: CsrfResponseData, 
    handleStompError: (frame: IFrame) => void, 
    handleConnect: () => void,
    handleDisconnect: () => void,
    handleWebSocketClose: () => void
): Client {
    return new Client({
        // Tells STOMP to use our SockJS instance
        webSocketFactory: () => socket,
        // Auto-reconnect after 1 second if connection drops
        reconnectDelay: 1000,
        // Send heartbeat to backend every 10secs to confirm connection is alive
        heartbeatOutgoing: 10000,
        // Expect to receive heartbeat from backend every 10 secs
        heartbeatIncoming: 10000,
        connectHeaders: {
            [csrfTokenData.headerName]: csrfTokenData.token
        },
        // Called if the server sends a STOMP error
        onStompError: handleStompError,
        onConnect: handleConnect,
        onDisconnect: handleDisconnect,
        onWebSocketClose: handleWebSocketClose
    });
}