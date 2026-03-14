import { useQuery } from "@tanstack/react-query";
import type { TopFivePlayersDto } from "../../../../types/dto/response/TopFivePlayersDto";
import { getTopFivePlayers } from "./getTopFivePlayers";
import { queryKeys } from "@/state/queryKeys";

export function useGetTopFivePlayers() {
    return useQuery<TopFivePlayersDto, Error>({
        queryKey: queryKeys.topFivePlayers,
        queryFn: getTopFivePlayers,
        retry: false,
        staleTime: 0
    })
}