import { useGetGameEvents } from "@/api/rest/game/events/query/useGetGameEvents";
import { InfiniteMessageList } from "../shared/InfiniteMessageList";
import { GameEventMessage } from "./GameEventMessage";
import { useInfiniteMessageList } from "@/hooks/global/useInfiniteMessageList";

export function HUDGameEventLog({
    gameId
}: {
    gameId: number
}) {
    const {data, isLoading, isError, error, hasNextPage, fetchNextPage, isFetchingNextPage, refetch } = useGetGameEvents(gameId);

    const { chatRef, sentinelRef, messages, isAtBottom, hasNewMessages, scrollToBottom, handleScroll } = useInfiniteMessageList({ data, hasNextPage, isFetchingNextPage, fetchNextPage, refetch });

    return (
        <div id="game-event-log" className="flex flex-col justify-between flex-1 min-h-0">
            <h2 className="card-header">Game Event Log</h2>
            <InfiniteMessageList
                chatRef={chatRef}
                sentinelRef={sentinelRef}
                messages={messages}
                isAtBottom={isAtBottom}
                hasNewMessages={hasNewMessages}
                onScroll={handleScroll}
                onScrollToBottom={scrollToBottom}
                renderMessage={(msg, index) => 
                    <GameEventMessage key={index} msg={msg} />
                }
            />
        </div>
    )
}