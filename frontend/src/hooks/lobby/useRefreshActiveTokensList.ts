import { queryKeys } from "@/state/queryKeys";
import type { UserDto } from "@/types/dto/entity/user/UserDto";
import type { QueryClient } from "@tanstack/react-query";
import { useEffect } from "react";

export function useRefreshActiveTokensList(
    queryClient: QueryClient,
    currentUser: UserDto,
) {
    // Interval to refresh active tokens display every minute
    useEffect(() => {
        const interval = setInterval(() => {
            queryClient.invalidateQueries({
                queryKey: queryKeys.userTokens(currentUser.id)}
            );
        }, 60 * 1000);
        return () => clearInterval(interval);
    }, [currentUser.id, queryClient]);
}