import { sendLobbyChatMessage } from "@/api/lobby/sendLobbyChatMessage";
import type { LobbyChatMessageRequestDto } from "@/types/dto/request/LobbyChatMessageRequestDto";
import type { LobbyChatSubmitMessageResponseDto } from "@/types/dto/response/LobbyChatSubmitMessageResponseDto";
import { useMutation } from "@tanstack/react-query";

export function useSendLobbyChatMessage() {
    return useMutation<LobbyChatSubmitMessageResponseDto, Error, LobbyChatMessageRequestDto>({
        mutationFn: ({lobbyId, userId, username, message}) => sendLobbyChatMessage(lobbyId, userId, username, message),
        onSuccess: (data: LobbyChatSubmitMessageResponseDto) => {
            // cache will be updated following websocket message receipt
        },
        onError: (error) => {
            alert(error.message);
        }
    })
}