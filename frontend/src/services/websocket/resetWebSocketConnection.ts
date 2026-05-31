import { stompClientFactory } from "@/factories/stompClientFactory";
import type { Client, Frame } from "@stomp/stompjs";

export async function resetWebSocketConnection(
    clientRef: React.RefObject<Client | null>,
    initialReconnectDelay: number,
    handleConnect: () => void,
    handleStompError: (frame: Frame) => void,
    handleDisconnect: () => void,
    handleWebSocketClose: () => void,
    handleWebSocketError: (event: any) => void
) {
    console.log("Resetting WebSocket connection...");

    // Clean up old client
    clientRef.current?.deactivate();
    clientRef.current = null;

    // Create new client
    clientRef.current = stompClientFactory(initialReconnectDelay, handleStompError, handleConnect, handleDisconnect, handleWebSocketClose, handleWebSocketError);
    // will trigger handleConnect
    clientRef.current.activate(); 
}