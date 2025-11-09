import { sendLobbyChatMessage } from "@/api/lobby/sendLobbyChatMessage";
import type { LobbyChatMessageDto } from "@/types/dto/entity/LobbyChatMessageDto";
import type { LobbyChatMessageRequestDto } from "@/types/dto/request/LobbyChatMessageRequestDto";
import { useMutation } from "@tanstack/react-query";

export function useSendLobbyChatMessage() {
    return useMutation<LobbyChatMessageDto, Error, LobbyChatMessageRequestDto>({
        mutationFn: ({lobbyId, userId, username, message}) => sendLobbyChatMessage(lobbyId, userId, username, message),
        onSuccess: (data: LobbyChatMessageDto) => {
            // cache will be updated following websocket message receipt
        },
        onError: (error) => {
            alert(error.message);
        }
    })
}