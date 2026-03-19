import { useQuery } from "@tanstack/react-query";
import type { UserActiveTokensDto } from "@/types/dto/response/UserActiveTokensDto";
import { queryKeys } from "@/state/queryKeys";
import { getActiveUserTokens } from "./getActiveUserTokens";

export function useGetActiveUserTokens(userId: number) {
    return useQuery<UserActiveTokensDto>({
        queryKey: queryKeys.userTokens(userId),
        queryFn: () => getActiveUserTokens(userId),
        refetchOnWindowFocus: true
    });
}