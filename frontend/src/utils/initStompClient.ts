import { Client, type IFrame, type IMessage, type StompSubscription } from "@stomp/stompjs";
import { handleUserWebSocketMessages } from "../services/websocket/handleUserWebSocketMessages";
import type { QueryClient } from "@tanstack/react-query";
import { stompClientFactory } from "../factories/stompClientFactory";
import { getCsrfToken } from "../api/csrf/getCsrfToken";

export async function initStompClient(
    socket: WebSocket,
    clientRef: React.RefObject<Client | null>,
    subscriptionsRef: React.RefObject<Map<string, StompSubscription>>,
    queryClient: QueryClient
): Promise<void> {
    
    // Get Xor encoded csrf token from the backend - xor encoded token is required for Spring Security websocket connections
    let csrfTokenData = await getCsrfToken();
    if (csrfTokenData === null) {
        console.warn("Missing CSRF token, cannot initialize STOMP client.");
        // Option 1: notify user
        alert("We couldn’t establish a secure connection. Please refresh the page.");
    }

    const handleStompError = (frame: IFrame) => {
        console.error('STOMP Error', frame.headers['message'], frame.body);
    }

    const handleConnect = () => {
        const topic = "/user/queue/updates";
        // Re-subscribe to all topics we were tracking before disconnect
        if (!clientRef.current) return;
        const newSub = clientRef.current.subscribe(topic, (message: IMessage) => {
            handleUserWebSocketMessages(JSON.parse(message.body), queryClient);
        });
        subscriptionsRef.current.set(topic, newSub);
    }
    
    try {
        // Create a new STOMP client that will use the SockJS socket
        clientRef.current = stompClientFactory(socket, csrfTokenData, handleStompError, handleConnect);
        // Activate the STOMP client - connection is started
        clientRef.current.activate();
    } catch (error) {
        console.error("Failed to initialize STOMP client", error);
    }
}