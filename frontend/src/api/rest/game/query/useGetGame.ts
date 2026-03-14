import { getGame } from "@/api/rest/game/query/getGame";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { GameHighlightedCellsResponseDto } from "@/types/dto/response/GameHighlightedCellsResponseDto";
import type { PublicGameState } from "@/types/game/GameTypes";
import { normalisePublicGameData, normalisePublicGameDataWithHighlightedCells } from "@/utils/game/normaliseGameState";
import { useQuery } from "@tanstack/react-query";
import { getGameHighlightedCells } from "../memory/query/getGameHighlightedCells";

export function useGetGame(gameId: number) {
    return useQuery<PublicGameState>({
        queryKey: ["game", gameId],
        queryFn: async () => {
            // Get DB game data and in memory game highlighted cell data and merge during normalisation
            const [gameData, gameHighlightedCells]: [GameDto, GameHighlightedCellsResponseDto] = await Promise.all([
                getGame(gameId),
                getGameHighlightedCells(gameId)
            ]);
            return normalisePublicGameDataWithHighlightedCells(gameData, gameHighlightedCells);
        },
        // Only run if gameId has a value
        enabled: !!gameId,
        retry: false,
        // cache is updated by websocket connection, therefore refetching data not required
        staleTime: Infinity,
    })
}