import { getCsrfToken } from "@/api/rest/csrf/getCsrfToken";
import { stompClientFactory } from "@/factories/stompClientFactory";
import { initWebSocket } from "@/utils/services/initWebSocket";
import type { Client, Frame } from "@stomp/stompjs";

export async function resetWebSocketConnection(
    clientRef: React.RefObject<Client | null>,
    handleConnect: () => void,
    handleStompError: (frame: Frame) => void
) {
    // Get a fresh WebSocket
    const newSocket = initWebSocket();

    // Fetch CSRF again if needed
    const csrfTokenData = await getCsrfToken();
    if (!csrfTokenData) return;

    // Clean up old client
    clientRef.current?.deactivate();

    // Create new client
    clientRef.current = stompClientFactory(newSocket, csrfTokenData, handleStompError, handleConnect);
    clientRef.current.reconnectDelay = 1000;
    // will trigger handleConnect
    clientRef.current.activate(); 
}