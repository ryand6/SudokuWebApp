import { apiFetch } from "@/api/apiFetch";
import type { GetGameChatMessagesResponseDto } from "@/types/dto/response/GetGameChatMessagesResponseDto";

export async function getGameChatMessages(gameId: number, pageNumber: number): Promise<GetGameChatMessagesResponseDto> {
    const response = await apiFetch(`/api/game/chat/get-chat-messages?gameId=${gameId}&page=${pageNumber}`, {
        method: "GET",
        credentials: "include",
        headers: { "Accept" : "application/json" },
    });
    return await response.json();
}