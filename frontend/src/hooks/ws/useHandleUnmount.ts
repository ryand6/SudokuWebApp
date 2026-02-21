import type { Client } from "@stomp/stompjs";
import { useEffect, type RefObject } from "react";

export function useHandleUnmount(clientRef: RefObject<Client | null>) {
    // Disconnect if component unmounts
    useEffect(() => {
        return () => {
            clientRef.current?.deactivate;
            clientRef.current = null;
        }
    }, []);
}