import { useWebSocketContext } from "@/context/WebSocketProvider";
import { useCallback } from "react";


export function useSimulateDisconnect() {
    const { clientRef } = useWebSocketContext();

    return useCallback(() => {
        const client = clientRef.current;
        if (!client) {
            console.warn("[WS Dev] No client found");
            return;
        }

        // Access underlying WebSocket — ungraceful close triggers onWebSocketClose + reconnect
        const ws = (client as any).webSocket as WebSocket | undefined;
        if (!ws) {
            console.warn("[WS Dev] No underlying WebSocket found, log client to inspect:", client);
            console.log("[WS Dev] Client keys:", Object.keys(client));
            return;
        }
        console.log("[WS Dev] Forcing WebSocket close. State:", ws.readyState);
        
        ws.close();
    }, [clientRef]);
}