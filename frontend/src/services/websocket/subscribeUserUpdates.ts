import type { Client, IMessage } from "@stomp/stompjs";
import type { RefObject } from "react";
import { handleUserWebSocketMessages } from "./handleUserWebSocketMessages";
import { QueryClient } from "@tanstack/react-query";

// Subscribes user to their updates topic using the client ref and stored the subscription in a refObject
export function subscribeUserUpdates(queryClient: QueryClient, clientRef: RefObject<Client | null>) {
    if (!clientRef.current) return;

    const topic = "/user/queue/updates";
    clientRef.current.subscribe(topic, (message: IMessage) => {
        handleUserWebSocketMessages(JSON.parse(message.body), queryClient);
    });
}