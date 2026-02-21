import type { Client } from "@stomp/stompjs";
import { useEffect, type RefObject } from "react";

export function useHandleUnload(clientRef: RefObject<Client | null>) {
    // Disconnects if browser/tab is closed
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