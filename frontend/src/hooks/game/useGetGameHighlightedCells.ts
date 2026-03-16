import { getGameHighlightedCells } from "@/api/rest/game/memory/query/getGameHighlightedCells";
import type { CellCoordinates } from "@/types/game/GameTypes";
import { useEffect, type Dispatch, type SetStateAction } from "react";

export function useGetGameHighlightedCells(
    gameId: number, 
    userId: number | undefined, 
    setGameHighlightedCells: Dispatch<SetStateAction<Map<number, CellCoordinates> | undefined>>

) {
    useEffect(() => {
        if (!userId) return;
        async function fetchData() {
            const data = await getGameHighlightedCells(gameId);
            if (!data) return;
            // Convert data structure to map for numeric keys when iterating entries
            const gameHighlightedCells: Map<number, CellCoordinates> = new Map<number, CellCoordinates>(
                Object.entries(data).map(([key, value]) => [Number(key), value])
            );
            setGameHighlightedCells(gameHighlightedCells);
        }
        fetchData();
    }, [gameId, userId, setGameHighlightedCells]);
}