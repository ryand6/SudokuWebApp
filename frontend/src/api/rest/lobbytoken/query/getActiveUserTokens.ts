import type { UserActiveTokensDto } from "@/types/dto/response/UserActiveTokensDto";

export async function getActiveUserTokens(userId: number): Promise<UserActiveTokensDto> {
    const response = await fetch(`/api/lobby/token/${userId}/get-active-tokens`, {
        method: "GET",
        credentials: "include",
        headers: { "Accept" : "application/json" }
    });
    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.errorMessage || "Failed to retrieve active tokens");
    }
    return await response.json();
}