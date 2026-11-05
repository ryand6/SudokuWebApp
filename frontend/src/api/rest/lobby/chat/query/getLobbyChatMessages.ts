import { apiFetch } from "@/api/apiFetch";
import type { LobbyGetChatMessagesResponseDto } from "@/types/dto/response/LobbyGetChatMessagesResponseDto";

export async function getLobbyChatMessages(lobbyId: number, pageNumber: number): Promise<LobbyGetChatMessagesResponseDto> {
    const response = await apiFetch(`/api/lobby/chat/get-chat-messages?lobbyId=${lobbyId}&page=${pageNumber}`, {
        method: "GET",
        credentials: "include",
        headers: { "Accept" : "application/json" },
    });
    return await response.json();
}