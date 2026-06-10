import { useGetGameChatMessages } from "@/api/rest/game/chat/query/useGetGameChatMessages";
import { sendGameChatMessage } from "@/api/ws/game/chat/sendGameChatMessage";
import { useWebSocketContext } from "@/context/WebSocketProvider"
import { useInfiniteMessageList } from "@/hooks/global/useInfiniteMessageList";
import { useMemo, useState } from "react";
import { InfiniteMessageList } from "@/components/global/InfiniteMessageList";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { groupMessages, type ChatMessageGroup } from "@/utils/game/infiniteDataUtils";
import type { GameChatMessageDto } from "@/types/dto/entity/game/GameChatMessageDto";
import { OutgoingMessageGroup } from "./OutgoingMessageGroup";
import { IncomingMessageGroup } from "./IncomingMessageGroup";
import type { PlayerColour } from "@/types/enum/PlayerColour";

export function HUDGameChat({
    gameId,
    userId,
    playerColours
}: {
    gameId: number,
    userId: number,
    playerColours: Record<number, PlayerColour> | undefined
}) {
    const { send } = useWebSocketContext();
    const [inputMessage, setInputMessage] = useState("");
    const { data, isLoading, isError, error, hasNextPage, fetchNextPage, isFetchingNextPage, refetch } = useGetGameChatMessages(gameId);
    const { chatRef, sentinelRef, messages, isAtBottom, hasNewMessages, scrollToBottom, handleScroll } = useInfiniteMessageList({ data, hasNextPage, isFetchingNextPage, fetchNextPage, refetch });

    const handleClick = () => {
        if (!inputMessage.trim()) return;
        sendGameChatMessage(send, gameId, inputMessage, "MESSAGE");
        // Clear text area 
        setInputMessage("");
        scrollToBottom();
    };

    const messageGroups: ChatMessageGroup[] = useMemo(() => {
        return groupMessages<GameChatMessageDto>(messages)
    }, [messages]);

    console.log("Message Groups: ", messageGroups);

    return (
        <div className="flex flex-col flex-1 bg-background rounded">
            <div className="flex w-full h-auto py-3 bg-sidebar rounded-t">
                <h3 className="pl-5 tracking-wider text-2xl font-extrabold font-display text-sidebar-foreground">
                    Game Chat
                </h3>
            </div>
            <InfiniteMessageList
                chatRef={chatRef}
                sentinelRef={sentinelRef}
                messages={messageGroups}
                isAtBottom={isAtBottom}
                hasNewMessages={hasNewMessages}
                onScroll={handleScroll}
                onScrollToBottom={scrollToBottom}
                renderMessage={(group, index) => {
                    return group.userId === userId ? (
                        <OutgoingMessageGroup key={index} messageGroup={group} playerColours={playerColours} />
                    ) : (
                        <IncomingMessageGroup key={index} messageGroup={group} playerColours={playerColours} />
                    )
                }}
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