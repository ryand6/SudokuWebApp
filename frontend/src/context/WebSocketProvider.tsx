import { Client, type IMessage, type StompSubscription } from "@stomp/stompjs";
import { createContext, useCallback, useContext, useMemo, useRef, useState, type RefObject } from "react";
import { useGetCurrentUser } from "../api/rest/users/query/useGetCurrentUser";
import { useQueryClient } from "@tanstack/react-query";
import { useHandleUnmount } from "@/hooks/ws/useHandleUnmount";
import { useHandleUnload } from "@/hooks/ws/useHandleUnload";
import { useInitClient } from "@/hooks/ws/useInitClient";
import { subscribeUserUpdates } from "@/services/websocket/subscribeUserUpdates";
import { subscribeUserErrors } from "@/services/websocket/subscribeUserErrors";

export type WebSocketContextType = {
    subscribe: (topic: string, onMessage: (body: any) => void) => StompSubscription | null;
    unsubscribe: (topic: string) => void;
    send: (destination: string, body: any) => void;
    isConnected: boolean,
    nextReconnectAt: number | null,
    clientRef: RefObject<Client | null>
}

export type StompSubscriptionDetails = {
    subscription: StompSubscription,
    subscriptionCallback: (body: any) => void
}

const BASE_RECONNECT_DELAY = 500;
export const MAX_RECONNECT_DELAY = 10000;

function calcInitialDelay() {
    return BASE_RECONNECT_DELAY + Math.random() * 500;
}

const WebSocketContext = createContext<WebSocketContextType | null>(null);

export function WebSocketProvider({ children }: { children : React.ReactNode }) {
    const { data: currentUser } = useGetCurrentUser();

    // Store singleton socket client in ref state so that re-renders don't affect the client
    const clientRef = useRef<Client | null>(null);
    // Persist map across app that keeps track of topics a user is subscribed to so they don't repeatedly subscribe to the same topic
    const subscriptionsRef = useRef<Map<string, StompSubscriptionDetails>>(new Map());
    const pendingQueueRef = useRef<Array<{ topic: string; callback: (body: any) => void }>>([]);

    const initialDelayRef = useRef<number>(calcInitialDelay());
    const currentDelayRef = useRef<number>(initialDelayRef.current);

    const [isConnected, setIsConnected] = useState(false);
    const [nextReconnectAt, setNextReconnectAt] = useState<number | null>(null);

    const queryClient = useQueryClient();

    // System subscriptions are recreated on every connect
    // subscriptionsRef contains only dynamic subscriptions that must survive reconnects
    const handleConnect = useCallback(() => {
        console.log("WebSocket connected");

        setIsConnected(true);
        setNextReconnectAt(null);
        // System level subscriptions resubscribed every reconnect
        subscribeUserUpdates(queryClient, clientRef);
        subscribeUserErrors(clientRef);
        // Handle dynamic subscriptions - these should re-subscribe to any existing subscriptions that were terminated due to a disconnect rather than an unsubscribe event e.g. lobby and games topics
        subscriptionsRef.current.forEach((subscriptionDetails: StompSubscriptionDetails, topic: string) => {
            const newSubscription = clientRef.current!.subscribe(topic, (msg: IMessage) => subscriptionDetails.subscriptionCallback(JSON.parse(msg.body)));
            const newSubscriptionDetails: StompSubscriptionDetails = {subscription: newSubscription, subscriptionCallback: subscriptionDetails.subscriptionCallback};
            subscriptionsRef.current.set(topic, newSubscriptionDetails);
        })
        // Subscribe to pending subscriptions that could not be completed before whilst client was not connected
        pendingQueueRef.current.forEach(({ topic, callback }) => {
            const subscription = clientRef.current!.subscribe(topic, (msg: IMessage) => callback(JSON.parse(msg.body)));
            const subscriptionDetails: StompSubscriptionDetails = {subscription: subscription, subscriptionCallback: callback};
            subscriptionsRef.current.set(topic, subscriptionDetails);
        });
        pendingQueueRef.current = [];
    }, [queryClient]);

    const recordDisconnect = useCallback(() => {
        setIsConnected(false);
        const nextAttempt = Date.now() + currentDelayRef.current;
        setNextReconnectAt(nextAttempt);
        currentDelayRef.current = Math.min(currentDelayRef.current * 2, MAX_RECONNECT_DELAY);
    }, []);

    const handleDisconnect = useCallback(() => recordDisconnect(), [recordDisconnect]);
    const handleWebSocketClose = useCallback(() => recordDisconnect(), [recordDisconnect]);

    useHandleUnmount(clientRef);
    useHandleUnload(clientRef);

    useInitClient(currentUser, clientRef, initialDelayRef.current, subscriptionsRef, pendingQueueRef, handleConnect, handleDisconnect, handleWebSocketClose);

    const subscribe = useCallback((topic: string, onMessage: (body: any) => void) => {
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
    }, []);

    const unsubscribe = useCallback((topic: string) => {
        const subscriptionDetails = subscriptionsRef.current.get(topic);
        if (subscriptionDetails) {
            subscriptionDetails.subscription.unsubscribe();
            subscriptionsRef.current.delete(topic);
        }
    }, []);

    const send = useCallback((destination: string, body: any) => {
        clientRef.current?.publish({
            destination: destination,
            body: JSON.stringify(body)
        });
    }, []);

    const value = useMemo(() => ({
        subscribe,
        unsubscribe,
        send,
        isConnected,
        nextReconnectAt,
        clientRef
    }), [subscribe, unsubscribe, send, isConnected, nextReconnectAt]);

    return (
        // Provide the subscribe, unsubscribe, and send methods to the rest of app via useContext
        <WebSocketContext.Provider value={value}>
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