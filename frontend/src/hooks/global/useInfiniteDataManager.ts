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
    setIsLoadingNextPage,
    reverseData
}: {
    data: InfiniteData<T[]> | undefined,
    hasNextPage: boolean,
    isFetchingNextPage: boolean,
    fetchNextPage: (options?: FetchNextPageOptions) => Promise<InfiniteQueryObserverResult<InfiniteData<T[]>, Error>>,
    refetch: () => void,
    isAtBottom: boolean,
    isLoadingNextPage: boolean,
    setIsLoadingNextPage: (value: boolean) => void,
    reverseData: boolean
}) {

    const { ref: sentinelRef, inView } = useInView({ threshold: 0 });

    const messages = useMemo(() => {
        if (!data) return [];
        if (!reverseData) return data.pages.flat();
        return data.pages.flat().reverse();
    }, [data]);

    useEffect(() => { 
        refetch(); 
    }, []);

    useEffect(() => {
        // isAtBottom check only required for messaging interfaces where newest messages are shown at bottom. Other displays load new pages whenever the bottom of the scroll is reached.
        if ((!reverseData || !isAtBottom) && inView && hasNextPage && !isFetchingNextPage && !isLoadingNextPage) {
            fetchNextPage();
            setIsLoadingNextPage(true);
        }
    }, [reverseData, isAtBottom, inView, hasNextPage, isFetchingNextPage, isLoadingNextPage]);

    return { sentinelRef, messages };
}