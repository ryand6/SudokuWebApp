import { useQuery } from "@tanstack/react-query";
import { getActiveUserTokens } from "@/api/rest/lobbytoken/query/getActiveUserTokens";
import type { UserActiveTokensDto } from "@/types/dto/response/UserActiveTokensDto";
import { queryKeys } from "@/state/queryKeys";

export function useGetActiveUserTokens(userId: number) {
    return useQuery<UserActiveTokensDto>({
        queryKey: queryKeys.userTokens(userId),
        queryFn: () => getActiveUserTokens(userId),
        refetchOnWindowFocus: true
    });
}