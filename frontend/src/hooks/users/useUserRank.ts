import { useQuery } from "@tanstack/react-query";
import { getUserRank } from "../../api/users/getUserRank";
import type { UserRankDto } from "../../types/dto/response/UserRankDto";

export function useUserRank() {
    return useQuery<UserRankDto, Error>({
        queryKey: ["userRank"],
        queryFn: getUserRank,
        retry: false,
        // refetch from DB every time the fetch is requested
        staleTime: 0,
    });
}