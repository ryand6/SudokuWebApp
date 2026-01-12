import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import type { FetchNextPageOptions, InfiniteData, InfiniteQueryObserverResult } from "@tanstack/react-query";
import { useEffect } from "react";

export function useHandleFetchNextLobbyListPage(
    inView: boolean,
    hasNextPage: boolean,
    isFetchingNextPage: boolean,
    fetchNextPage: (options?: FetchNextPageOptions | undefined) => Promise<InfiniteQueryObserverResult<InfiniteData<LobbyDto[], unknown>, Error>>
) {
    useEffect(() => {
        if (inView && hasNextPage && !isFetchingNextPage) {
            fetchNextPage();
        }
    }, [inView, hasNextPage, fetchNextPage, isFetchingNextPage]);
}