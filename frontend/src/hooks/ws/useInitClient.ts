import type { StompSubscriptionDetails } from "@/context/WebSocketProvider";
import { subscribeUserErrors } from "@/services/websocket/subscribeUserErrors";
import { subscribeUserUpdates } from "@/services/websocket/subscribeUserUpdates";
import type { UserDto } from "@/types/dto/entity/user/UserDto";
import { initStompClient } from "@/services/websocket/initStompClient";
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
            if (clientRef.current) {
                clientRef.current?.deactivate();
                clientRef.current = null;
                subscriptionsRef.current.clear();
                pendingQueueRef.current = [];
            }
            return;
        }

        if (clientRef.current) return;

        // NOTE:
        // - System subscriptions are recreated on every connect
        // - subscriptionsRef contains ONLY dynamic subscriptions that must survive reconnects
        const handleConnect = () => {
            console.log("WebSocket connected");

            setIsConnected(true);

            // System level subscriptions resubscribed every reconnect
            subscribeUserUpdates(queryClient, clientRef);
            subscribeUserErrors(clientRef);

            console.log("Subscriptions Ref size on connect:", subscriptionsRef.current.size);

            console.log("Pending queue size on connect:", pendingQueueRef.current.length);

            // Handle dynamic subscriptions - these should re-subscribe to any existing subscriptions that were terminated due to a disconnect rather than an unsubscribe event e.g. lobby and games topics
            subscriptionsRef.current.forEach((subscriptionDetails: StompSubscriptionDetails, topic: string) => {

                console.log(`Resubscribing to topic ${topic} with callback ${subscriptionDetails.subscriptionCallback.toString()}`);

                const newSubscription = clientRef.current!.subscribe(topic, (msg: IMessage) => subscriptionDetails.subscriptionCallback(JSON.parse(msg.body)));
                const newSubscriptionDetails: StompSubscriptionDetails = {subscription: newSubscription, subscriptionCallback: subscriptionDetails.subscriptionCallback};
                subscriptionsRef.current.set(topic, newSubscriptionDetails);
            })

            // Subscribe to pending subscriptions that could not be completed before whilst client was not connected
            pendingQueueRef.current.forEach(({ topic, callback }) => {

                console.log(`Subscribing to pending topic ${topic} with callback ${callback.toString()}`);

                const subscription = clientRef.current!.subscribe(topic, (msg: IMessage) => callback(JSON.parse(msg.body)));
                const subscriptionDetails: StompSubscriptionDetails = {subscription: subscription, subscriptionCallback: callback};
                subscriptionsRef.current.set(topic, subscriptionDetails);
            });
            pendingQueueRef.current = [];
        }

        initStompClient(clientRef, handleConnect, handleDisconnect, handleWebSocketClose);
        
    }, [currentUser]);
}