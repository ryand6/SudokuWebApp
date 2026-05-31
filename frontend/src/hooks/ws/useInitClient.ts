import type { StompSubscriptionDetails } from "@/context/WebSocketProvider";
import type { UserDto } from "@/types/dto/entity/user/UserDto";
import { initStompClient } from "@/services/websocket/initStompClient";
import type { Client } from "@stomp/stompjs";
import { useEffect, type RefObject } from "react";

export function useInitClient(
    currentUser: UserDto | undefined,
    clientRef: RefObject<Client | null>,
    initialReconnectDelay: number,
    subscriptionsRef: RefObject<Map<string, StompSubscriptionDetails>>,
    pendingQueueRef: RefObject<{topic: string; callback: (body: any) => void;}[]>,
    handleConnect: () => void,
    handleDisconnect: () => void,
    handleWebSocketClose: () => void
 ) {
    // Effect handles socket lifecycle - exists whilst there is an authenticated session
    useEffect(() => {
        if (!currentUser) {
            if (clientRef.current) {
                clientRef.current?.deactivate();
                clientRef.current = null;
                subscriptionsRef.current.clear();
                pendingQueueRef.current = [];
            }
            return;
        }
        if (clientRef.current) return;
        initStompClient(clientRef, initialReconnectDelay, handleConnect, handleDisconnect, handleWebSocketClose);
    }, [currentUser]);
}