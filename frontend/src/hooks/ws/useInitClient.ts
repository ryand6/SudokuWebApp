import type { StompSubscriptionDetails } from "@/context/WebSocketProvider";
import { subscribeUserErrors } from "@/services/websocket/subscribeUserErrors";
import { subscribeUserUpdates } from "@/services/websocket/subscribeUserUpdates";
import type { UserDto } from "@/types/dto/entity/UserDto";
import { initStompClient } from "@/utils/services/initStompClient";
import { initWebSocket } from "@/utils/services/initWebSocket";
import type { Client, IMessage } from "@stomp/stompjs";
import type { QueryClient } from "@tanstack/react-query";
import { useEffect, type Dispatch, type RefObject, type SetStateAction } from "react";

export function useInitClient(
    currentUser: UserDto | undefined,
    clientRef: RefObject<Client | null>,
    setIsConnected: Dispatch<SetStateAction<boolean>>,
    queryClient: QueryClient,
    subscriptionsRef: RefObject<Map<string, StompSubscriptionDetails>>,
    pendingQueueRef: RefObject<{topic: string; callback: (body: any) => void;}[]>,
    handleDisconnect: () => void,
    handleWebSocketClose: () => void
 ) {
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

            // Subscribe to pending subscriptions that could not be completed before whilst client was not connected
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
}