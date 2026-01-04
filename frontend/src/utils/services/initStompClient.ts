import { Client, type IFrame, type IMessage, type StompSubscription } from "@stomp/stompjs";
import { stompClientFactory } from "../../factories/stompClientFactory";
import { getCsrfToken } from "../../api/rest/csrf/getCsrfToken";

export async function initStompClient(
    socket: WebSocket,
    clientRef: React.RefObject<Client | null>,
    handleConnect: () => void
): Promise<void> {
    
    // Get Xor encoded csrf token from the backend - xor encoded token is required for Spring Security websocket connections
    let csrfTokenData = await getCsrfToken();
    if (csrfTokenData === null) {
        console.warn("Missing CSRF token, cannot initialize STOMP client.");
        alert("We couldn’t establish a secure connection. Please refresh the page.");
    }

    const handleStompError = (frame: IFrame) => {
        console.error('STOMP Error', frame.headers['message'], frame.body);
    }
    
    try {
        // Create a new STOMP client that will use the SockJS socket
        clientRef.current = stompClientFactory(socket, csrfTokenData, handleStompError, handleConnect);

        // Attempt to reconnect every 1s if connection drops
        clientRef.current.reconnectDelay = 1000;

        // clientRef.current.debug = (str) => {
        //     console.debug("[STOMP]", str);
        // }

        clientRef.current.onStompError = handleStompError;

        // Activate the STOMP client - connection is started
        clientRef.current.activate();
    } catch (error) {
        console.error("Failed to initialize STOMP client", error);
    }
}