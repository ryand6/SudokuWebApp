import { Textarea } from "../ui/textarea";
import { Button } from "../ui/button";
import { useState } from "react";
import { sendLobbyChatMessage } from "@/api/ws/lobby/sendLobbyChatMessage";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { useGetLobbyChatMessages } from "@/api/rest/lobby/chat/query/useGetLobbyChatMessages";
import { useInfiniteMessageList } from "@/hooks/global/useInfiniteMessageList";
import { InfiniteMessageList } from "../shared/InfiniteMessageList";
import { LobbyChatMessage } from "./LobbyChatMessage";
import { LobbyInfoMessage } from "./LobbyInfoMessage";

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
        sendLobbyChatMessage(send, lobbyId, userId, inputMessage);
        // Clear text area 
        setInputMessage("");
        scrollToBottom();
    };

    return (
        <div id="lobby-chat-panel" className="flex flex-col justify-between lobby-card flex-1 min-h-0">
            <h2 className="card-header">Lobby Chat</h2>
            <InfiniteMessageList
                chatRef={chatRef}
                sentinelRef={sentinelRef}
                messages={messages}
                isAtBottom={isAtBottom}
                hasNewMessages={hasNewMessages}
                onScroll={handleScroll}
                onScrollToBottom={scrollToBottom}
                renderMessage={(msg, index) => {
                    switch (msg.messageType) {
                        case "MESSAGE": return <LobbyChatMessage key={index} msg={msg} />;
                        case "INFO": return <LobbyInfoMessage key={index} msg={msg} />;
                    }
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