import { useQuery } from "@tanstack/react-query";
import { getUserRank } from "../../api/rest/users/getUserRank";
import type { UserRankDto } from "../../types/dto/response/UserRankDto";

export function useGetUserRank() {
    return useQuery<UserRankDto, Error>({
        queryKey: ["userRank"],
        queryFn: getUserRank,
        retry: false,
        // refetch from DB every time the fetch is requested
        staleTime: 0,
    });
}