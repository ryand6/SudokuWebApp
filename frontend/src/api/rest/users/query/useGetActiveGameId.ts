import { queryKeys } from "@/state/queryKeys";
import { useQuery } from "@tanstack/react-query";
import { getActiveGameId } from "./getActiveGameId";

export function useGetActiveGameId() {
    return useQuery<number | null, Error>({
        queryKey: queryKeys.userActiveGameId,
        queryFn: getActiveGameId,
        retry: false,
        // refetch from DB every time the fetch is requested
        staleTime: 0,
    });
}