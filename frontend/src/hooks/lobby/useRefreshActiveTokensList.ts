import { queryKeys } from "@/state/queryKeys";
import type { QueryClient } from "@tanstack/react-query";
import { useEffect } from "react";

export function useRefreshActiveTokensList(
    queryClient: QueryClient,
    userId: number,
) {
    // Interval to refresh active tokens display every minute
    useEffect(() => {
        const interval = setInterval(() => {
            queryClient.invalidateQueries({
                queryKey: queryKeys.userTokens(userId)}
            );
        }, 60 * 1000);
        return () => clearInterval(interval);
    }, [userId, queryClient]);
}