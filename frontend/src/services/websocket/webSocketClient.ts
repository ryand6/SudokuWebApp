import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

// singleton client declared in top level of module scope - one client exists per a user's session
let webSocketClient: Client | null = null;

export const getWebSocketClient = (): Client => {
    // Only allow one instance of the client
    if (!webSocketClient) {
        // Create a SockJS socket pointing to the server endpoint
        const socket = new SockJS("http:localhost:8080/ws");
        // Create a new STOMP client that will use the SockJS socket
        webSocketClient = new Client({
            // Tells STOMP to use our SockJS instance
            webSocketFactory: () => socket,
            // Auto-reconnect after 5 seconds if connection drops
            reconnectDelay: 5000,
            // Called if the server sends a STOMP error
            onStompError: (frame) => {
                console.error('STOMP Error', frame.headers['message'], frame.body);
            },
        });
        // Activate the STOMP client - connection is started
        webSocketClient.activate();
    }
    return webSocketClient;
};