import { sendLobbyChatMessage } from "@/api/lobby/sendLobbyChatMessage";
import { addLobbyMessage } from "@/services/lobby/lobbyMessagesService";
import type { LobbyChatMessageRequestDto } from "@/types/dto/request/LobbyChatMessageRequestDto";
import type { LobbyChatSubmitMessageResponseDto } from "@/types/dto/response/LobbyChatSubmitMessageResponseDto";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export function useSendLobbyChatMessage() {
    const queryClient = useQueryClient();

    return useMutation<LobbyChatSubmitMessageResponseDto, Error, LobbyChatMessageRequestDto>({
        mutationFn: ({lobbyId, userId, username, message}) => sendLobbyChatMessage(lobbyId, userId, username, message),
        onSuccess: (data: LobbyChatSubmitMessageResponseDto) => {
            const updatedMessages = addLobbyMessage(data.lobbyId, data.username, data.message);
            queryClient.setQueryData(["lobbyChat", data.lobbyId], updatedMessages);
        },
        onError: (error) => {
            alert(error.message);
        }
    })
}