import { useGetGameEvents } from "@/api/rest/game/events/query/useGetGameEvents";
import { GameEventMessage } from "./GameEventMessage";
import { useInfiniteMessageList } from "@/hooks/global/useInfiniteMessageList";
import { InfiniteMessageList } from "@/components/global/InfiniteMessageList";
import type { PlayerColour } from "@/types/enum/PlayerColour";
export function HUDGameEventLog({
    gameId,
    playerColours
}: {
    gameId: number,
    playerColours: Record<number, PlayerColour> | undefined
}) {
    const {data, isLoading, isError, error, hasNextPage, fetchNextPage, isFetchingNextPage, refetch } = useGetGameEvents(gameId);

    const { chatRef, sentinelRef, messages, isAtBottom, hasNewMessages, scrollToBottom, handleScroll } = useInfiniteMessageList({ data, hasNextPage, isFetchingNextPage, fetchNextPage, refetch });

    const totalEvents = data?.pages.reduce((acc, page) => acc + page.length, 0) ?? 0;

    return (
        <div className="flex flex-col flex-1 bg-background rounded">
            <div className="flex w-full h-auto py-3 bg-sidebar rounded-t justify-between items-center px-5">
                <h3 className="tracking-wider text-2xl font-semibold font-display text-sidebar-foreground">
                    Event Log
                </h3>
                <span className="text-muted-foreground font-sans text-md pr-10">
                    {totalEvents} events
                </span>
            </div>
            <InfiniteMessageList
                chatRef={chatRef}
                sentinelRef={sentinelRef}
                messages={messages}
                isAtBottom={isAtBottom}
                hasNewMessages={hasNewMessages}
                onScroll={handleScroll}
                onScrollToBottom={scrollToBottom}
                renderMessage={(msg, index) => 
                    <GameEventMessage key={index} msg={msg} playerColours={playerColours} />

                }
            />
        </div>
    )
}