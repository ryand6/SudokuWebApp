import { Client, type IMessage, type StompSubscription } from "@stomp/stompjs";
import { createContext, useContext, useRef, useState } from "react";
import { useGetCurrentUser } from "../api/rest/users/query/useGetCurrentUser";
import { useQueryClient } from "@tanstack/react-query";
import { useHandleUnmount } from "@/hooks/ws/useHandleUnmount";
import { useHandleUnload } from "@/hooks/ws/useHandleUnload";
import { useInitClient } from "@/hooks/ws/useInitClient";

export type WebSocketContextType = {
    subscribe: (topic: string, onMessage: (body: any) => void) => StompSubscription | null;
    unsubscribe: (topic: string) => void;
    send: (destination: string, body: any) => void;
    isConnected: boolean
}

export type StompSubscriptionDetails = {
    subscription: StompSubscription,
    subscriptionCallback: (body: any) => void
}

const WebSocketContext = createContext<WebSocketContextType | null>(null);

export function WebSocketProvider({ children }: { children : React.ReactNode }) {
    const { data: currentUser } = useGetCurrentUser();

    // Store singleton socket client in ref state so that re-renders don't affect the client
    const clientRef = useRef<Client | null>(null);
    // Persist map across app that keeps track of topics a user is subscribed to so they don't repeatedly subscribe to the same topic
    const subscriptionsRef = useRef<Map<string, StompSubscriptionDetails>>(new Map());
    const pendingQueueRef = useRef<Array<{ topic: string; callback: (body: any) => void }>>([]);

    const [isConnected, setIsConnected] = useState(false);

    const queryClient = useQueryClient();

    const handleDisconnect = () => {
        setIsConnected(false);
    }

    const handleWebSocketClose = () => {
        setIsConnected(false);
    }

    useHandleUnmount(clientRef);

    useHandleUnload(clientRef);

    useInitClient(currentUser, clientRef, setIsConnected, queryClient, subscriptionsRef, pendingQueueRef, handleDisconnect, handleWebSocketClose);

    const subscribe = (topic: string, onMessage: (body: any) => void) => {
        // If the user is already subscribed, return that subscription 
        if (subscriptionsRef.current.has(topic)) return subscriptionsRef.current.get(topic)?.subscription!;

        const client = clientRef.current;
        // No socket client connection established to fulfill subscribe
        if (!client || !client.connected) {
            // Add it to pending queue so that once connection is established, subscriptions are fulfilled
            pendingQueueRef.current.push({ topic, callback: onMessage });
            return null;
        }

        const subscription: StompSubscription = client.subscribe(topic, (message: IMessage) => {
            // Provide parsed IMessages to the provided callback function to handle based on message type
            onMessage(JSON.parse(message.body));
        });

        const subscriptionDetails: StompSubscriptionDetails = {subscription: subscription, subscriptionCallback: onMessage};

        // Add the subscription to the map if newly subscribed
        subscriptionsRef.current.set(topic, subscriptionDetails);
        return subscription;
    };

    const unsubscribe = (topic: string) => {
        const subscriptionDetails = subscriptionsRef.current.get(topic);
        if (subscriptionDetails) {
            subscriptionDetails.subscription.unsubscribe();
            subscriptionsRef.current.delete(topic);
        }
    };

    const send = (destination: string, body: any) => {
        clientRef.current?.publish({
            destination: destination,
            body: JSON.stringify(body)
        });
    };

    return (
        // Provide the subscribe, unsubscribe, and send methods to the rest of app via useContext
        <WebSocketContext.Provider value={{ subscribe, unsubscribe, send, isConnected }}>
            {children}
        </WebSocketContext.Provider>
    );
};

// Helper function to provide the subscribe, unsubscribe and send helper methods as context to rest of app
export function useWebSocketContext() {
    const ctx = useContext(WebSocketContext);
    if (!ctx) throw new Error("useWebSocketContext must be used inside WebSocketProvider wrapper");
    return ctx;
}