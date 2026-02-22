import { useQuery } from "@tanstack/react-query";
import { getTopFivePlayers } from "../../api/rest/users/getTopFivePlayers";
import type { TopFivePlayersDto } from "../../../../types/dto/response/TopFivePlayersDto";

export function useGetTopFivePlayers() {
    return useQuery<TopFivePlayersDto, Error>({
        queryKey: ["topFivePlayers"],
        queryFn: getTopFivePlayers,
        retry: false,
        staleTime: 0
    })
}