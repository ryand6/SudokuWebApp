import type { PublicLobbiesListDto } from "@/types/dto/response/PublicLobbiesListDto";

export async function getTenPublicLobbies(pageNumber: number): Promise<PublicLobbiesListDto> {
    const response = await fetch(`/api/lobby/public/get-active-lobbies?page=${pageNumber}`, {
        method: "GET",
        headers: { "Accept" : "application/json" },
    });
    return response.json();
}