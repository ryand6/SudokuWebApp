import { useGetLeaderboardRows } from "@/api/rest/leaderboards/query/useGetLeaderboardRows";
import { useGetUserLeaderboardRow } from "@/api/rest/leaderboards/query/useGetUserLeaderboardRow";
import { useGetCurrentUser } from "@/api/rest/users/query/useGetCurrentUser";
import { InfiniteMessageList } from "@/components/global/InfiniteMessageList";
import { LeaderboardRow } from "@/components/leaderboards/LeaderboardRow";
import { MobileLeaderboardRow } from "@/components/leaderboards/MobileLeaderboardRow";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { Spinner } from "@/components/ui/spinner";
import { useInfiniteMessageList } from "@/hooks/global/useInfiniteMessageList";
import { useIsMobile } from "@/hooks/global/useIsMobile";
import { type LeaderboardRowDto } from "@/types/dto/entity/leaderboards/LeaderboardRowDto";
import { gameModes, type GameMode } from "@/types/enum/GameMode";
import { wordToProperCase } from "@/utils/string/wordToProperCase";
import { IconZoom } from "@tabler/icons-react";
import { useEffect, useState } from "react";

export function LeaderboardsPage() {
    const { data: user } = useGetCurrentUser();
    const isMobile = useIsMobile();
    const [selectedMode, setSelectedMode] = useState<GameMode>("CLASSIC");
    const [searchTerm, setSearchTerm] = useState("");
    const [filteredMessages, setFilteredMessages] = useState<LeaderboardRowDto[]>([]);
    const { data: userLeaderboardRecord, isLoading: isLoadingUserRecord } = useGetUserLeaderboardRow(user?.id ?? 0, selectedMode);
    const {data, isLoading: isLoadingRows, isError, error, hasNextPage, fetchNextPage, isFetchingNextPage, refetch } = useGetLeaderboardRows(selectedMode);
    const reverseData = false;
    const iconSize = isMobile ? 16 : 24;
    
    const { chatRef, sentinelRef, messages, isAtBottom, hasNewMessages, scrollToBottom, handleScroll } = useInfiniteMessageList({ data, hasNextPage, isFetchingNextPage, fetchNextPage, refetch, reverseData });

    useEffect(() => {
        if (messages) {
            setFilteredMessages(messages);
        }
        if (searchTerm) {
            setFilteredMessages(messages.filter((msg) => msg.username.includes(searchTerm)));
        }
    }, [messages, searchTerm]);

    if (!user) return;

    console.log("filtered messages length:", filteredMessages.length);
    console.log("filtered messages:", filteredMessages);

    return (
        <div className="flex flex-col flex-1 bg-background">
            <div className="flex items-center border-t-2 border-b-2 border-muted bg-muted/30 gap-8 w-full px-5">
                <div className="flex flex-1 gap-3 px-4 py-2">
                    {
                        gameModes.map((mode, index) => (
                            <div 
                                key={index}
                                className={`inline-flex items-center justify-center w-full py-1 rounded-full 
                                font-display border-2 border-muted cursor-pointer
                                ${selectedMode === mode ? "bg-primary text-primary-foreground border-primary" : "bg-background text-muted-foreground hover:bg-primary/40 hover:border-primary/40 hover:text-primary-foreground"}`}
                                onClick={() => setSelectedMode(mode)}
                            >
                                <span className="text-md text-center font-medium tracking-wide">{wordToProperCase(mode)}</span>
                            </div>
                        ))
                    }
                </div>
                <div className="flex flex-1 justify-end items-center py-2">
                    <div className="flex border-1 gap-3 border-muted bg-background rounded-lg py-2 px-3 text-muted-foreground items-center">
                        <IconZoom size={iconSize} />
                        <div className="rounded-lg bg-muted/50 p-2">
                            <input type="text" placeholder="Search player..." className="outline-0" onChange={(e) => setSearchTerm(e.target.value)} value={searchTerm} />
                        </div>
                    </div>
                </div>
            </div>
            <div className="flex bg-sidebar text-sidebar-foreground py-4 px-6 justify-between">
                {
                    isLoadingUserRecord ? (
                        <Spinner />
                    ) : !userLeaderboardRecord ? (
                        <div className="flex flex-col">
                            <div>
                                {user.username}
                            </div>
                            <div>
                                No ranked stats for this game mode yet
                            </div>
                        </div>
                    ) : (
                        <div className="flex gap-4 items-center">
                            <div className="text-sidebar-primary">
                                #{userLeaderboardRecord.rank.toLocaleString()}
                            </div>
                            <div className="flex flex-col">
                                <div>
                                    {user.username}
                                </div>
                                <div className="flex">
                                    
                                </div>
                            </div>
                        </div>
                    )
                }
            </div>
            <div className="flex justify-between py-2 px-4 border-b-2 border-t-2 border-muted bg-muted/30">
                <div className="flex flex-1 justify-start items-center gap-4">
                    <div className="flex flex-1 md:max-w-[10%] justify-center tracking-wider text-muted-foreground font-semibold">
                        #
                    </div>
                    <div className="flex flex-1 md:max-w-[10%] justify-center tracking-wider text-muted-foreground font-semibold">
                        PLAYER
                    </div>
                </div>
                <div className="flex flex-1 items-center gap-4">
                    <div className="flex flex-1 justify-center tracking-wider text-muted-foreground font-semibold">
                        SCORE
                    </div>
                    <div className="flex flex-1 justify-center tracking-wider text-muted-foreground font-semibold">
                        GAMES
                    </div>
                    {
                        isMobile && (
                            <div className="flex flex-1 justify-center"></div>
                        )
                    }
                    {
                        !isMobile && (
                            <>
                                <div className="flex flex-1 justify-center tracking-wider text-muted-foreground font-semibold">
                                    WINS
                                </div>
                                <div className="flex flex-1 justify-center tracking-wider text-muted-foreground font-semibold">
                                    LOSSES
                                </div> 
                                <div className="flex flex-1 justify-center tracking-wider text-muted-foreground font-semibold">
                                    DRAWS
                                </div>
                                <div className="flex flex-1 justify-center tracking-wider text-muted-foreground font-semibold">
                                    MAX WIN STREAK
                                </div>
                            </>
                        )
                    }
                </div>
            </div>
            {
                isLoadingRows && (
                    <div className="flex w-full h-full items-center justify-center py-5">
                        <SpinnerButton />
                    </div>
                )
            }
            <InfiniteMessageList
                chatRef={chatRef}
                sentinelRef={sentinelRef}
                messages={filteredMessages}
                isAtBottom={isAtBottom}
                hasNewMessages={hasNewMessages}
                onScroll={handleScroll}
                onScrollToBottom={scrollToBottom}
                renderMessage={(msg, index) => {
                    if (isMobile) return <MobileLeaderboardRow key={index} data={msg} userId={user?.id} />;
                    return <LeaderboardRow key={index} data={msg} userId={user?.id} isMobile={isMobile} />;
                }}
                reverseData={false}
            />
            <div className="py-10"></div>
        </div>
    )
}