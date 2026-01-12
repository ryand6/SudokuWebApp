import type { UserDto } from "@/types/dto/entity/UserDto";
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
                queryKey: ["user", currentUser.id, "tokens"]}
            );
        }, 60 * 1000);
        return () => clearInterval(interval);
    }, [currentUser.id, queryClient]);
}