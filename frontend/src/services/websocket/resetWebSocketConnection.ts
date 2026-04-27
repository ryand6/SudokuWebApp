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
    console.log("Resetting WebSocket connection...");

    // Fetch CSRF again if needed
    const csrfTokenData = await getCsrfToken();
    if (!csrfTokenData) return;

    // Clean up old client
    clientRef.current?.deactivate();
    clientRef.current = null;

    // Create new client
    clientRef.current = stompClientFactory(csrfTokenData, handleStompError, handleConnect, handleDisconnect, handleWebSocketClose);
    // will trigger handleConnect
    clientRef.current.activate(); 
}