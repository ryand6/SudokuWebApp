import type { GameEventDto } from "@/types/dto/entity/game/GameEventDto";
import type { InfiniteData } from "@tanstack/react-query";
import type { GameEventLog } from "../gameEvents";
import { handleNewInfiniteData } from "@/utils/game/infiniteDataUtils";

export function gameEventsCacheReducer(
    existingData: InfiniteData<GameEventDto[]> | undefined,
    event: GameEventLog
) {
    switch (event.type) {
        case "GAME_EVENT": {
            return handleNewInfiniteData<GameEventDto>(
                existingData, 
                event, 
                (event) => event.sequenceNumber
            );
        }
    }
}