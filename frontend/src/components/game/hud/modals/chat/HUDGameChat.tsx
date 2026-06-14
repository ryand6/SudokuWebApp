import { useGetGameChatMessages } from "@/api/rest/game/chat/query/useGetGameChatMessages";
import { sendGameChatMessage } from "@/api/ws/game/chat/sendGameChatMessage";
import { useWebSocketContext } from "@/context/WebSocketProvider"
import { useInfiniteMessageList } from "@/hooks/global/useInfiniteMessageList";
import { useMemo, useState } from "react";
import { InfiniteMessageList } from "@/components/global/InfiniteMessageList";
import { groupMessages, type ChatMessageGroup } from "@/utils/game/infiniteDataUtils";
import type { GameChatMessageDto } from "@/types/dto/entity/game/GameChatMessageDto";
import { OutgoingMessageGroup } from "../../../../chat/OutgoingMessageGroup";
import { IncomingMessageGroup } from "../../../../chat/IncomingMessageGroup";
import type { PlayerColour } from "@/types/enum/PlayerColour";
import { InfoMessageGroup } from "../../../../chat/InfoMessageGroup";
import { TypeMessageBar } from "../../../../chat/TypeMessageBar";
import { QuickMessageBar } from "../../../../chat/QuickMessageBar";
import { MessageTypeSelectorRow } from "@/components/chat/MessageTypeSelectorRow";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import type { GameMode } from "@/types/enum/GameMode";

export function HUDGameChat({
    gameId,
    userId,
    playerColours,
    gameMode
}: {
    gameId: number,
    userId: number,
    playerColours: Record<number, PlayerColour> | undefined,
    gameMode: GameMode
}) {
    const { send } = useWebSocketContext();
    const [inputMessage, setInputMessage] = useState("");
    const [isQuickMessage, setIsQuickMessage] = useState(true);
    const { data, isLoading, isError, error, hasNextPage, fetchNextPage, isFetchingNextPage, refetch } = useGetGameChatMessages(gameId);
    const { chatRef, sentinelRef, messages, isAtBottom, hasNewMessages, scrollToBottom, handleScroll } = useInfiniteMessageList({ data, hasNextPage, isFetchingNextPage, fetchNextPage, refetch });

    const handleTypeClick = () => {
        if (!inputMessage.trim()) return;
        sendGameChatMessage(send, gameId, inputMessage, "MESSAGE");
        // Clear text area 
        setInputMessage("");
        scrollToBottom();
    };

    const handleQuickClick = (message: string) => {
        if (!message.trim()) return;
        sendGameChatMessage(send, gameId, message, "TEMPLATE");
        // Clear text area 
        setInputMessage("");
        scrollToBottom();
    };

    const VERSUS_QUICK_MESSAGES: string[] = [
        "Good luck",
        "GG",
        "Well played",
        "Didn't see that coming",
        "You're fast",
        "Tough board this one",
        "I'm struggling here",
        "I'm cooked",
        "That was close",
        "Respect",
        "I am unstoppable",
        "How are you so quick",
        "I'll get the next one",
        "This board is brutal",
        "Rematch?",
    ];

    const TEAMWORK_QUICK_MESSAGES: string[] = [
        "On it",
        "Good catch",
        "My bad",
        "Almost there",
        "Legend",
        "Let's go",
        "Nice teamwork",
        "Top banana",
        "Good spot",
        "We're doing great",
        "Took a while but got it",
        "Appreciate the help",
        "Fine, I'll do it myself",
    ];

    const messagingTemplate = gameMode === "TIMEATTACK" ? TEAMWORK_QUICK_MESSAGES : VERSUS_QUICK_MESSAGES;

    const messageGroups: ChatMessageGroup[] = useMemo(() => {
        return groupMessages<GameChatMessageDto>(messages)
    }, [messages]);

    return (
        <div className="flex flex-col flex-1 bg-background rounded">
            <div className="flex w-full h-auto py-3 bg-sidebar rounded-t">
                <h3 className="pl-5 tracking-wider text-2xl font-extrabold font-display text-sidebar-foreground">
                    Game Chat
                </h3>
            </div>
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
                        <OutgoingMessageGroup key={index} messageGroup={group} playerColours={playerColours} isLastGroup={isLastGroup} />
                    ) : (
                        <IncomingMessageGroup key={index} messageGroup={group} playerColours={playerColours} isLastGroup={isLastGroup} />
                    )
                }}
            />
            <div className="flex flex-col py-3 px-4 border-t-1 border-muted bg-card">
                <MessageTypeSelectorRow isQuickMessage={isQuickMessage} setIsQuickMessage={setIsQuickMessage} />
                {
                    isQuickMessage ? (
                        <QuickMessageBar messageTemplates={messagingTemplate} setInputMessage={setInputMessage} handleClick={handleQuickClick} />
                    ) : (
                        <TypeMessageBar inputMessage={inputMessage} setInputMessage={setInputMessage} handleClick={handleTypeClick} />
                    )
                }
            </div>
        </div>
    )

}