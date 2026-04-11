import { useState } from "react";
import type { FetchNextPageOptions, InfiniteData, InfiniteQueryObserverResult } from "@tanstack/react-query";
import { useInfiniteDataManager } from "@/hooks/global/useInfiniteDataManager";
import { useInfiniteScroll } from "@/hooks/global/useInfiniteScroll";


export function useInfiniteMessageList<T>({ 
    data, 
    hasNextPage, 
    isFetchingNextPage, 
    fetchNextPage, 
    refetch 
}: {
    data: InfiniteData<T[]> | undefined;
    hasNextPage: boolean;
    isFetchingNextPage: boolean;
    fetchNextPage: (options?: FetchNextPageOptions) => Promise<InfiniteQueryObserverResult<InfiniteData<T[]>, Error>>;
    refetch: () => void;
}) {
    const [isAtBottom, setIsAtBottom] = useState(true);
    const [isLoadingNextPage, setIsLoadingNextPage] = useState(false);

    const { sentinelRef, messages } = useInfiniteDataManager<T>({
        data, hasNextPage, isFetchingNextPage, fetchNextPage, refetch, isAtBottom, isLoadingNextPage, setIsLoadingNextPage
    });

    const { chatRef, hasNewMessages, scrollToBottom, handleScroll } = useInfiniteScroll<T>({
        isFetchingNextPage, messages, isAtBottom, setIsAtBottom, setIsLoadingNextPage
    });

    return { chatRef, sentinelRef, messages, isAtBottom, hasNewMessages, scrollToBottom, handleScroll };
}