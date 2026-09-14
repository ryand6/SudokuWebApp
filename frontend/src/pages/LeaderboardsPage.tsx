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
    const iconSize = isMobile ? 12 : 24;
    
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

    return (
        <div className="flex flex-col flex-1 bg-background font-display">
            <div className={`flex ${isMobile ? "flex-col px-3" : "px-5 gap-8"} items-center border-t-2 border-b-2 border-muted bg-muted/30 w-full`}>
                <div className="flex flex-1 gap-2 md:gap-3 md:px-4 py-2">
                    {
                        gameModes.map((mode, index) => (
                            <div 
                                key={index}
                                className={`inline-flex items-center justify-center w-full py-1 px-3 rounded-full 
                                            font-display border-1 border-muted cursor-pointer
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
            <div className={`flex bg-sidebar text-sidebar-foreground ${isMobile ? "justify-between px-4" : "px-10 gap-12"} py-4`}>
                {
                    isLoadingUserRecord ? (
                        <Spinner />
                    ) : !userLeaderboardRecord ? (
                        <div className="flex gap-4 items-center">
                            <div className="text-sidebar-primary text-2xl font-semibold">
                                #_
                            </div>
                            <div className="flex flex-col">
                                <div className="font-semibold tracking-wider text-xl">
                                    {user.username}
                                </div>
                                <div className="text-s md:text-md text-muted">
                                    No ranked stats for this game mode yet
                                </div>
                            </div>
                        </div>
                    ) : (
                        <>
                            <div className="flex gap-4 items-center">
                                <div className="text-sidebar-primary text-2xl font-semibold">
                                    #{userLeaderboardRecord.rank.toLocaleString()}
                                </div>
                                <div className="flex flex-col">
                                    <div className="font-semibold tracking-wider text-xl">
                                        {user.username}
                                    </div>
                                    {
                                        isMobile ? (
                                            <>
                                                <div className="text-s text-muted flex items-center gap-2 justify-self-center">
                                                    <span>{userLeaderboardRecord.gamesPlayed.toLocaleString() + (userLeaderboardRecord.gamesPlayed === 1 ? " game" : " games")}</span>
                                                    <span>&middot;</span>
                                                    <span>{userLeaderboardRecord.wins.toLocaleString() + "W"}</span>
                                                    <span>&middot;</span>
                                                    <span>{userLeaderboardRecord.losses.toLocaleString() + "L"}</span>
                                                    <span>&middot;</span>
                                                    <span>{userLeaderboardRecord.draws.toLocaleString() + "D"}</span>
                                                </div>
                                                <div className="text-s text-muted flex items-center gap-2 justify-self-center">
                                                    <span>{Math.round((userLeaderboardRecord.wins / userLeaderboardRecord.gamesPlayed) * 100).toLocaleString() + "% win rate"}</span>
                                                    <span>&middot;</span>
                                                    <span>{userLeaderboardRecord.maxWinStreak.toLocaleString() + " best streak"}</span>
                                                </div>
                                            </>
                                            
                                        ) : (
                                            <div className="flex text-muted items-center gap-2 md:gap-3 justify-self-center">
                                                <span>{userLeaderboardRecord.gamesPlayed.toLocaleString() + (userLeaderboardRecord.gamesPlayed === 1 ? " Game" : " Games")}</span>
                                                <span>&middot;</span>
                                                <span>{userLeaderboardRecord.wins.toLocaleString() + (userLeaderboardRecord.wins === 1 ? " Win" : " Wins")}</span>
                                                <span>&middot;</span>
                                                <span>{userLeaderboardRecord.losses.toLocaleString() + (userLeaderboardRecord.losses === 1 ? " Loss" : " Losses")}</span>
                                                <span>&middot;</span>
                                                <span>{userLeaderboardRecord.draws.toLocaleString() + (userLeaderboardRecord.draws === 1 ? " Draw" : " Draws")}</span>
                                                <span>&middot;</span>
                                                <span>{Math.round((userLeaderboardRecord.wins / userLeaderboardRecord.gamesPlayed) * 100).toLocaleString() + "% Win Rate"}</span>
                                                <span>&middot;</span>
                                                <span>{userLeaderboardRecord.maxWinStreak.toLocaleString() + " Max Win Streak"}</span>
                                            </div>
                                        )
                                    }
                                </div>
                            </div>
                            <div className="flex flex-col justify-center">
                                <div className="text-2xl font-semibold text-sidebar-primary">
                                    {userLeaderboardRecord.totalScore.toLocaleString()}
                                </div>
                                <div className="text-muted">
                                    Total score
                                </div>
                            </div>
                        </>
                        
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
                                    WIN RATE
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
                    if (isMobile) return <MobileLeaderboardRow key={index} data={msg} userId={user?.id} gameMode={selectedMode} />;
                    return <LeaderboardRow key={index} data={msg} userId={user?.id} isMobile={isMobile} />;
                }}
                reverseData={false}
            />
            <div className="py-10"></div>
        </div>
    )
}