import { queryKeys } from "@/state/queryKeys";
import type { LobbyDetailsDto } from "@/types/dto/response/LobbyDetailsDto";
import { useQuery } from "@tanstack/react-query";
import { getActiveLobby } from "./getActiveLobby";

export function useGetActiveLobby() {
    return useQuery<LobbyDetailsDto, Error>({
        queryKey: queryKeys.userActiveLobby,
        queryFn: getActiveLobby,
        retry: false,
        // refetch from DB every time the fetch is requested
        staleTime: 0,
    });
}