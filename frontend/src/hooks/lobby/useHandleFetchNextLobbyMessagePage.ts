import type { LobbyChatMessageDto } from "@/types/dto/entity/LobbyChatMessageDto";
import type { FetchNextPageOptions, InfiniteData, InfiniteQueryObserverResult } from "@tanstack/react-query";
import { useEffect, type SetStateAction } from "react";

export function useHandleFetchNextLobbyMessagePage(
    isAtBottom: boolean,
    inView: boolean,
    hasNextPage: boolean,
    isFetchingNextPage: boolean,
    isLoadingNextPage: boolean,
    fetchNextPage: (options?: FetchNextPageOptions | undefined) => Promise<InfiniteQueryObserverResult<InfiniteData<LobbyChatMessageDto[], unknown>, Error>>,
    setIsLoadingNextPage: (value: SetStateAction<boolean>) => void
) {
    // custom hook used to handle fetching next page of lobby messages
    useEffect(() => {
        // To prevent more pages being loaded on mount, isAtBottom is set to true to begin with
        if (!isAtBottom && inView && hasNextPage && !isFetchingNextPage && !isLoadingNextPage) {
            fetchNextPage();
            setIsLoadingNextPage(true);
        }
    }, [inView, hasNextPage, fetchNextPage, isFetchingNextPage]);
}