import { Client, type IFrame } from "@stomp/stompjs";
import { stompClientFactory } from "../../factories/stompClientFactory";
import { resetWebSocketConnection } from "@/services/websocket/resetWebSocketConnection";

export async function initStompClient(
    clientRef: React.RefObject<Client | null>,
    initialReconnectDelay: number,
    handleConnect: () => void,
    handleDisconnect: () => void,
    handleWebSocketClose: () => void,
    handleWebSocketError: (event: any) => void
): Promise<void> {
    const handleStompError = (frame: IFrame) => {
        console.error('STOMP Error', frame.headers['message'], frame.body);
        // force reset to ensure reconnect is consistent and clean
        resetWebSocketConnection(clientRef, initialReconnectDelay, handleConnect, handleStompError, handleDisconnect, handleWebSocketClose, handleWebSocketError);
    }
    
    try {
        // Create a new STOMP client that will use the SockJS socket
        clientRef.current = stompClientFactory(initialReconnectDelay, handleStompError, handleConnect, handleDisconnect, handleWebSocketClose, handleWebSocketError);
        clientRef.current.onStompError = handleStompError;
        // Activate the STOMP client - connection is started
        clientRef.current.activate();
    } catch (error) {
        console.error("Failed to initialize STOMP client", error);
    }
}