import { useQueryClient } from "@tanstack/react-query";
import { useWebSocketContext } from "./WebSocketProvider";
import { handleUserWebSocketMessages } from "../services/websocket/handleUserWebSocketMessages";
import { useEffect } from "react";

// Side effect only component that listens to currentUser cache in React Query and tries to subscribe the user to their socket channel whenever the currentUser query succeeds 
// Used to ensure a user is subscribed to their unique channel from the moment their data is stored in React Query cache
export function GlobalUserSubscriptionHandler() {
    const queryClient = useQueryClient();
    const { subscribe } = useWebSocketContext();

    useEffect(() => {
        // Store return function in variable which unsubscribes from the query cache, to be called upon cleanup
        const unsubscribe = queryClient.getQueryCache().subscribe((event) => {
            const query = event?.query;
            if (!query) return;

            // Make sure it's the query we care about
            if (query.queryKey[0] !== "currentUser") return;

            // Only run when data is available
            if (query.state.status === "success" && query.state.data) {
                subscribe(`/user/queue/updates`, (msg) => {
                    handleUserWebSocketMessages(msg);
                });
            }
        });
        // Cleanup function
        return unsubscribe;
    }, [queryClient, subscribe]);

    // Don't render anything
    return null;
}