import type { Client } from "@stomp/stompjs";
import { useEffect, type RefObject } from "react";

// Disconnect if component unmounts
export function useHandleUnmount(clientRef: RefObject<Client | null>) {
    useEffect(() => {
        return () => {
            console.log("Component unmounting, disconnecting from WebSocket...");

            clientRef.current?.deactivate();
            clientRef.current = null;
        }
    }, []);
}