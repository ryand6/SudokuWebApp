import type { LobbyChatMessageDto } from "@/types/dto/entity/LobbyChatMessageDto";
import { useEffect, type SetStateAction } from "react";

export function useHandleNewChatMessages(
    chatElement: HTMLDivElement | null,
    isAtBottom: boolean,
    setHasNewMessages: (value: SetStateAction<boolean>) => void,
    scrollToBottom: () => void,
    messages: LobbyChatMessageDto[]
) {
    useEffect(() => {
        if (!chatElement) return;
        // If the user is not at bottom of chat window and new messages have been received, set flag
        if (!isAtBottom){
            setHasNewMessages(true);
        } else {
            // If near bottom, always scroll to bottom when a new message is received to keep updates real time
            scrollToBottom();
        }
    }, [messages]);
}