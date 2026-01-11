import { Client, type IFrame } from "@stomp/stompjs";
import type { CsrfResponseData } from "../types/dto/auth/CsrfResponseData";


// Factory for creating StompJS client with callback functions to handle OnStompError and onConnect
export function stompClientFactory(
    socket: WebSocket, 
    csrfTokenData: CsrfResponseData, 
    handleStompError: (frame: IFrame) => void, 
    handleConnect: () => void
): Client {
    return new Client({
        // Tells STOMP to use our SockJS instance
        webSocketFactory: () => socket,
        // Auto-reconnect after 1 second if connection drops
        reconnectDelay: 1000,
        connectHeaders: {
            [csrfTokenData.headerName]: csrfTokenData.token
        },
        // Called if the server sends a STOMP error
        onStompError: handleStompError,
        onConnect: handleConnect
    });
}