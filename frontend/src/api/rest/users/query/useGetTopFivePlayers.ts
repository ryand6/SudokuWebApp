import { useQuery } from "@tanstack/react-query";
import type { TopFivePlayersDto } from "../../../../types/dto/response/TopFivePlayersDto";
import { getTopFivePlayers } from "./getTopFivePlayers";

export function useGetTopFivePlayers() {
    return useQuery<TopFivePlayersDto, Error>({
        queryKey: ["topFivePlayers"],
        queryFn: getTopFivePlayers,
        retry: false,
        staleTime: 0
    })
}