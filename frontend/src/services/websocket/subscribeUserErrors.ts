import type { Client, IMessage, StompSubscription } from "@stomp/stompjs";
import type { RefObject } from "react";
import { handleUserWebSocketErrors } from "./handleUserWebSocketErrors";

// Subscribes user to their updates topic using the client ref and stored the subscription in a refObject
export function subscribeUserErrors(clientRef: RefObject<Client | null>, subscriptionsRef: RefObject<Map<string, StompSubscription>>) {
    if (!clientRef.current) return;

    const topic = "/user/queue/errors";
    const newSub = clientRef.current.subscribe(topic, (error: IMessage) => {
        handleUserWebSocketErrors(JSON.parse(error.body));
    });
    subscriptionsRef.current.set(topic, newSub);
}