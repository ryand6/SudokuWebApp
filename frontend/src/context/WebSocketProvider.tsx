import { Client, type IMessage, type StompSubscription } from "@stomp/stompjs";
import { createContext, useContext, useEffect, useRef } from "react";
import { useCurrentUser } from "../hooks/useCurrentUser";
import { useQueryClient } from "@tanstack/react-query";
import { initWebSocket } from "../utils/initWebSocket";
import { initStompClient } from "../utils/initStompClient";

type WebSocketContextType = {
    subscribe: (topic: string, onMessage: (body: any) => void) => StompSubscription | null;
    unsubscribe: (topic: string) => void;
    send: (destination: string, body: any) => void;
}

const WebSocketContext = createContext<WebSocketContextType | null>(null);

export function WebSocketProvider({ children }: { children : React.ReactNode }) {
    const { data: currentUser } = useCurrentUser();
    // Store singleton socket client in ref state so that re-renders don't affect the client
    const clientRef = useRef<Client | null>(null);
    // Persist map across app that keeps track of topics a user is subscribed to so they don't repeatedly subscribe to the same topic
    const subscriptionsRef = useRef<Map<string, StompSubscription>>(new Map());

    const queryClient = useQueryClient();

    // Effect handles socket lifecycle - exists whilst there is an authenticated session
    useEffect(() => {
        if (!currentUser) {
            // don't create websocket connection until user is authenticated
            return;
        }
        // Only want to set up socket and stomp client if stomp client doesn't already exist
        if (clientRef.current) return;
        const socket = initWebSocket();

        initStompClient(socket, clientRef, subscriptionsRef, queryClient);

        // Cleanup if user logs out
        return () => {
            clientRef.current?.deactivate();
            clientRef.current = null;
            subscriptionsRef.current.clear();
        }
    }, [currentUser]);

    const subscribe = (topic: string, onMessage: (body: any) => void) => {
        // If the user is already subscribed, return that subscription - exclamation mark used to confirm the return type will never be undefined
        if (subscriptionsRef.current.has(topic)) return subscriptionsRef.current.get(topic)!;
        const client = clientRef.current;
        // No socket client connection established to fulfill subscribe
        if (!client || !client.connected) return null;
        const subscription: StompSubscription = client.subscribe(topic, (message: IMessage) => {
            // Provide parsed IMessages to the provided callback function to handle based on message type
            onMessage(JSON.parse(message.body));
        });
        // Add the subscription to the map if newly subscribed
        subscriptionsRef.current.set(topic, subscription);
        return subscription;
    };

    const unsubscribe = (topic: string) => {
        // If the topic has been subscribed to by the user, unsubscribe and remove from the tracking Maps
        const subscription = subscriptionsRef.current.get(topic);
        if (subscription) {
            subscription.unsubscribe();
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
        // Provide the subscribe, unsubscribe, and send methods as context to the rest of app via useContext
        <WebSocketContext.Provider value={{ subscribe, unsubscribe, send }}>
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