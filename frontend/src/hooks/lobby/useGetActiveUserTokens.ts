import { useQuery } from "@tanstack/react-query";
import { getActiveUserTokens } from "@/api/rest/lobby/getActiveUserTokens";
import type { UserActiveTokensDto } from "@/types/dto/response/UserActiveTokensDto";

export function useGetActiveUserTokens(userId: number) {
    return useQuery<UserActiveTokensDto>({
        queryKey: ["user", userId, "tokens"],
        queryFn: () => getActiveUserTokens(userId),
        refetchOnWindowFocus: true
    });
}