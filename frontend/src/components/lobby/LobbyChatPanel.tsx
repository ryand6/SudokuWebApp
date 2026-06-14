import { Textarea } from "../ui/textarea";
import { Button } from "../ui/button";
import { useMemo, useState } from "react";
import { sendLobbyChatMessage } from "@/api/ws/lobby/sendLobbyChatMessage";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { useGetLobbyChatMessages } from "@/api/rest/lobby/chat/query/useGetLobbyChatMessages";
import { useInfiniteMessageList } from "@/hooks/global/useInfiniteMessageList";
import { InfiniteMessageList } from "../global/InfiniteMessageList";
import { groupMessages, type ChatMessageGroup } from "@/utils/game/infiniteDataUtils";
import type { LobbyChatMessageDto } from "@/types/dto/entity/lobby/LobbyChatMessageDto";
import { SpinnerButton } from "../ui/custom/SpinnerButton";
import { InfoMessageGroup } from "../chat/InfoMessageGroup";
import { OutgoingMessageGroup } from "../chat/OutgoingMessageGroup";
import { IncomingMessageGroup } from "../chat/IncomingMessageGroup";

export function LobbyChatPanel({
    lobbyId, 
    userId
}: {
    lobbyId: number, 
    userId: number
}) {
    const { send } = useWebSocketContext();
    const [inputMessage, setInputMessage] = useState("");
    const {data, isLoading, isError, error, hasNextPage, fetchNextPage, isFetchingNextPage, refetch } = useGetLobbyChatMessages(lobbyId);
    const { chatRef, sentinelRef, messages, isAtBottom, hasNewMessages, scrollToBottom, handleScroll } = useInfiniteMessageList({ data, hasNextPage, isFetchingNextPage, fetchNextPage, refetch });

    const handleClick = () => {
        if (!inputMessage.trim()) return;
        sendLobbyChatMessage(send, lobbyId, inputMessage);
        // Clear text area 
        setInputMessage("");
        scrollToBottom();
    };

    const messageGroups: ChatMessageGroup[] = useMemo(() => {
        return groupMessages<LobbyChatMessageDto>(messages)
    }, [messages]);

    return (
        <div id="lobby-chat-panel" className="flex flex-col justify-between lobby-card flex-1 min-h-0">
            <h2 className="card-header">Lobby Chat</h2>
            {
                isLoading && <SpinnerButton />
            }
            <InfiniteMessageList
                chatRef={chatRef}
                sentinelRef={sentinelRef}
                messages={messageGroups}
                isAtBottom={isAtBottom}
                hasNewMessages={hasNewMessages}
                onScroll={handleScroll}
                onScrollToBottom={scrollToBottom}
                renderMessage={(group, index) => {
                    const isLastGroup: boolean = index === messageGroups.length - 1;
                    return group.messageType === "INFO" ? (
                        <InfoMessageGroup key={index} messageGroup={group} />
                    ) : group.userId === userId ? (
                        <OutgoingMessageGroup key={index} messageGroup={group} playerColours={undefined} isLastGroup={isLastGroup} />
                    ) : (
                        <IncomingMessageGroup key={index} messageGroup={group} playerColours={undefined} isLastGroup={isLastGroup} />
                    )
                }}
            />
            <div className="flex flex-col justify-between gap-1">
                <Textarea 
                    id="lobby-chat-input" 
                    placeholder="Type your message here."
                    value={inputMessage} 
                    onChange={(e) => {
                        setInputMessage(e.target.value);
                    }}
                    onKeyDown={(e) => {
                        if (e.key === "Enter" && !e.shiftKey) {
                            e.preventDefault();
                            handleClick();
                        }
                    }}
                />
                <Button 
                    onClick={handleClick}
                    className="cursor-pointer"
                >
                    Send message
                </Button>
            </div>
        </div>
    )
}