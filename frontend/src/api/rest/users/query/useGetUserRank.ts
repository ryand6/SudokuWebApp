import { useQuery } from "@tanstack/react-query";
import { getUserRank } from "./getUserRank";
import type { UserRankDto } from "../../../../types/dto/response/UserRankDto";
import { queryKeys } from "@/state/queryKeys";

export function useGetUserRank() {
    return useQuery<UserRankDto, Error>({
        queryKey: queryKeys.userRank,
        queryFn: getUserRank,
        retry: false,
        // refetch from DB every time the fetch is requested
        staleTime: 0,
    });
}