import type { Client } from "@stomp/stompjs";
import { useEffect, type RefObject } from "react";

// Disconnect if component unmounts
export function useHandleUnmount(clientRef: RefObject<Client | null>) {
    useEffect(() => {
        return () => {
            clientRef.current?.deactivate;
            clientRef.current = null;
        }
    }, []);
}