import { useEffect, useMemo } from "react";
import { useInView } from "react-intersection-observer";
import type { FetchNextPageOptions, InfiniteData, InfiniteQueryObserverResult } from "@tanstack/react-query";


export function useInfiniteDataManager<T>({
    data, 
    hasNextPage, 
    isFetchingNextPage, 
    fetchNextPage, 
    refetch,
    isAtBottom, 
    isLoadingNextPage, 
    setIsLoadingNextPage
}: {
    data: InfiniteData<T[]> | undefined;
    hasNextPage: boolean;
    isFetchingNextPage: boolean;
    fetchNextPage: (options?: FetchNextPageOptions) => Promise<InfiniteQueryObserverResult<InfiniteData<T[]>, Error>>;
    refetch: () => void;
    isAtBottom: boolean;
    isLoadingNextPage: boolean;
    setIsLoadingNextPage: (value: boolean) => void;
}) {

    const { ref: sentinelRef, inView } = useInView({ threshold: 0 });

    const messages = useMemo(() => {
        if (!data) return [];
        return data.pages.flat().reverse();
    }, [data]);

    useEffect(() => { 
        refetch(); 
    }, []);

    useEffect(() => {
        if (!isAtBottom && inView && hasNextPage && !isFetchingNextPage && !isLoadingNextPage) {
            fetchNextPage();
            setIsLoadingNextPage(true);
        }
    }, [inView, hasNextPage, isFetchingNextPage]);

    return { sentinelRef, messages };
}