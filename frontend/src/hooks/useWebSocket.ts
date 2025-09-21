import { useEffect } from 'react';
import { getWebSocketClient } from '../services/websocket/webSocketClient';
import type { IMessage } from '@stomp/stompjs';

export function useUserWebSocket (
    topic: string,
    onMessage: (body: any) => void
) {
    // Callback function that parses the JSON STOMP IMessage body to JS object then passes to the provided onMessage function which handles what do with the message contents
    const subscribeCallback = (message: IMessage) => {
        onMessage(JSON.parse(message.body));
    };

    useEffect(() => {
        const client = getWebSocketClient();
        const subscription = client.subscribe(topic, subscribeCallback);
        // cleanup on unmount
        return () => subscription.unsubscribe(); 
    }, [topic, onMessage]);
};