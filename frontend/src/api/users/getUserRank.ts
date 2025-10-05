import type { UserRankDto } from "../../types/dto/response/UserRankDto";

export async function getUserRank(): Promise<UserRankDto> {
    const response = await fetch("/api/users/get-user-rank", {
        method: "GET",
        credentials: "include",
        headers: { "Accept": "application/json" },
    });
    if (response.status === 404) {
        throw new Error("UserSetupRequired");
    }
    return await response.json(); 
}