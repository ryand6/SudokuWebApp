import { Client, type IFrame } from "@stomp/stompjs";
import { stompClientFactory } from "../../factories/stompClientFactory";
import { getCsrfToken } from "../../api/rest/csrf/getCsrfToken";
import { resetWebSocketConnection } from "@/services/websocket/resetWebSocketConnection";

export async function initStompClient(
    socket: WebSocket,
    clientRef: React.RefObject<Client | null>,
    handleConnect: () => void,
    handleDisconnect: () => void,
    handleWebSocketClose: () => void,
): Promise<void> {
    
    // Get Xor encoded csrf token from the backend - xor encoded token is required for Spring Security websocket connections
    let csrfTokenData = await getCsrfToken();
    if (csrfTokenData === null) {
        console.warn("Missing CSRF token, cannot initialize STOMP client.");
        alert("We couldn’t establish a secure connection. Please refresh the page.");
        return;
    }

    const handleStompError = (frame: IFrame) => {
        console.error('STOMP Error', frame.headers['message'], frame.body);
        // force reset to ensure reconnect is consistent and clean
        resetWebSocketConnection(clientRef, handleConnect, handleStompError);
    }
    
    try {
        // Create a new STOMP client that will use the SockJS socket
        clientRef.current = stompClientFactory(socket, csrfTokenData, handleStompError, handleConnect, handleDisconnect, handleWebSocketClose);
        
        clientRef.current.onStompError = handleStompError;

        // Activate the STOMP client - connection is started
        clientRef.current.activate();
    } catch (error) {
        console.error("Failed to initialize STOMP client", error);
    }
}