import { getCsrfToken } from "@/api/rest/csrf/query/getCsrfToken";
import { stompClientFactory } from "@/factories/stompClientFactory";
import type { Client, Frame } from "@stomp/stompjs";

export async function resetWebSocketConnection(
    clientRef: React.RefObject<Client | null>,
    handleConnect: () => void,
    handleStompError: (frame: Frame) => void,
    handleDisconnect: () => void,
    handleWebSocketClose: () => void,
) {
    // Fetch CSRF again if needed
    const csrfTokenData = await getCsrfToken();
    if (!csrfTokenData) return;

    // Clean up old client
    clientRef.current?.deactivate();

    // Create new client
    clientRef.current = stompClientFactory(csrfTokenData, handleStompError, handleConnect, handleDisconnect, handleWebSocketClose);
    clientRef.current.reconnectDelay = 1000;
    // will trigger handleConnect
    clientRef.current.activate(); 
}