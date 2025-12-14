import { requestJoinCode } from "@/api/lobby/requestJoinCode";
import type { RequestLobbyTokenDto } from "@/types/dto/request/RequestLobbyTokenDto";
import type { UserActiveTokensDto } from "@/types/dto/response/UserActiveTokensDto";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export type TokenWithExpiry = {
        token: string,
        expiresAt: number
}

export function useRequestJoinCode() {
    const queryClient = useQueryClient();

    return useMutation<void, Error, RequestLobbyTokenDto>({
        mutationFn: async ({lobbyId, userId}) => {
            const newToken = await requestJoinCode(lobbyId, userId);
            const queryKey = ["user", userId, "tokens"];
            // Store token expiry date with token and remove any expired tokens whilst updating cache
            queryClient.setQueryData<UserActiveTokensDto>(queryKey, (existing) => {
                const now = Date.now();
                // 10 minutes TTL
                const expiresAt = now + 10 * 60 * 1000;
                const tokenWithExpiry = {...newToken, expiresAt};
                const activeTokens = existing?.activeTokens.filter(token => token.expiresAt >= now) ?? [];
                return {
                    activeTokens: [...activeTokens, tokenWithExpiry]
                };
            });
        },
        onError: (error) => {
            alert(error.message);
        }
    })
}