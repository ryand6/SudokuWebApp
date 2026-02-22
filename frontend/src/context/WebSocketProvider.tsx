import { Client, type IMessage, type StompSubscription } from "@stomp/stompjs";
import { createContext, useContext, useEffect, useRef, useState } from "react";
import { useGetCurrentUser } from "../hooks/users/useGetCurrentUser";
import { initWebSocket } from "../utils/services/initWebSocket";
import { initStompClient } from "../utils/services/initStompClient";
import { subscribeUserUpdates } from "@/services/websocket/subscribeUserUpdates";
import { useQueryClient } from "@tanstack/react-query";
import { subscribeUserErrors } from "@/services/websocket/subscribeUserErrors";
import { useHandleUnmount } from "@/hooks/ws/useHandleUnmount";
import { useHandleUnload } from "@/hooks/ws/useHandleUnload";

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

    // Effect handles socket lifecycle - exists whilst there is an authenticated session
    useEffect(() => {
        if (!currentUser) {
            return;
        }

        if (clientRef.current) return;
        const socket = initWebSocket();

        // NOTE:
        // - System subscriptions are recreated on every connect
        // - subscriptionsRef contains ONLY dynamic subscriptions that must survive reconnects
        const handleConnect = () => {

            setIsConnected(true);

            // System level subscriptions resubscribed every reconnect
            subscribeUserUpdates(queryClient, clientRef);
            subscribeUserErrors(clientRef);

            // Handle dynamic subscriptions - these should re-subscribe to any existing subscriptions that were terminated due to a disconnect rather than an unsubscribe event e.g. lobby and games topics
            subscriptionsRef.current.forEach((subscriptionDetails: StompSubscriptionDetails, topic: string) => {
                const newSubscription = clientRef.current!.subscribe(topic, (msg: IMessage) => subscriptionDetails.subscriptionCallback(JSON.parse(msg.body)));
                const newSubscriptionDetails: StompSubscriptionDetails = {subscription: newSubscription, subscriptionCallback: subscriptionDetails.subscriptionCallback};
                subscriptionsRef.current.set(topic, newSubscriptionDetails);
            })

            /// Subscribe to pending subscriptions that could not be completed before whilst client was not connected
            pendingQueueRef.current.forEach(({ topic, callback }) => {
                const subscription = clientRef.current!.subscribe(topic, (msg: IMessage) => callback(JSON.parse(msg.body)));
                const subscriptionDetails: StompSubscriptionDetails = {subscription: subscription, subscriptionCallback: callback};
                subscriptionsRef.current.set(topic, subscriptionDetails);
            });
            pendingQueueRef.current = [];
        }

        initStompClient(socket, clientRef, handleConnect, handleDisconnect, handleWebSocketClose);

        // Cleanup if user logs out
        return () => {
            clientRef.current?.deactivate();
            clientRef.current = null;
            subscriptionsRef.current.clear();
            pendingQueueRef.current = [];
        }
    }, [currentUser]);

    const subscribe = (topic: string, onMessage: (body: any) => void) => {
        // If the user is already subscribed, return that subscription - exclamation mark used to confirm the return type will never be undefined
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