import { useGetGameChatMessages } from "@/api/rest/game/chat/query/useGetGameChatMessages";
import { sendGameChatMessage } from "@/api/ws/game/chat/sendGameChatMessage";
import { useWebSocketContext } from "@/context/WebSocketProvider"
import { useInfiniteMessageList } from "@/hooks/global/useInfiniteMessageList";
import { useState } from "react";
import { InfiniteMessageList } from "../shared/InfiniteMessageList";
import { Textarea } from "../ui/textarea";
import { Button } from "../ui/button";
import { GameChatMessage } from "./GameChatMessage";

export function HUDGameChat({
    gameId,
    userId
}: {
    gameId: number,
    userId: number
}) {
    const { send } = useWebSocketContext();
    const [inputMessage, setInputMessage] = useState("");
    const { data, isLoading, isError, error, hasNextPage, fetchNextPage, isFetchingNextPage, refetch } = useGetGameChatMessages(gameId);
    const { chatRef, sentinelRef, messages, isAtBottom, hasNewMessages, scrollToBottom, handleScroll } = useInfiniteMessageList({ data, hasNextPage, isFetchingNextPage, fetchNextPage, refetch });

    const handleClick = () => {
        if (!inputMessage.trim()) return;
        sendGameChatMessage(send, gameId, userId, inputMessage, "MESSAGE");
        // Clear text area 
        setInputMessage("");
        scrollToBottom();
    };

    return (
        <div id="game-chat-panel" className="flex flex-col justify-between gap-1 flex-1 min-h-0 m-2">
            <h2 className="card-header">Game Chat</h2>
            <InfiniteMessageList
                chatRef={chatRef}
                sentinelRef={sentinelRef}
                messages={messages}
                isAtBottom={isAtBottom}
                hasNewMessages={hasNewMessages}
                onScroll={handleScroll}
                onScrollToBottom={scrollToBottom}
                renderMessage={(msg, index) => 
                    <GameChatMessage key={index} msg={msg} />
                }
            />
            <div className="flex flex-col justify-between gap-1">
                <Textarea 
                    id="game-chat-input" 
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