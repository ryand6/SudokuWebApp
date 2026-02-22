import type { Client } from "@stomp/stompjs";
import { useEffect, type RefObject } from "react";

// Disconnects if browser/tab is closed
export function useHandleUnload(clientRef: RefObject<Client | null>) {
    useEffect(() => {
        const handleUnload = () => {
            clientRef.current?.deactivate;
            clientRef.current = null;
        }
        window.addEventListener("beforeunload", handleUnload);
        return () => {
            window.removeEventListener("beforeunload", handleUnload);
        }
    }, []);
}