import { gamePlayerStateCacheDispatcher } from "@/state/game/player/gamePlayerStateCacheDispatcher";
import { notificationEmitter } from "@/utils/game/gameNotificationUtils";
import type { QueryClient } from "@tanstack/react-query";

export function handleGamePlayerStateWebSocketMessages(message: any, queryClient: QueryClient, gameId: number, userId: number) {

    console.log(message);

    switch (message.type) {
        case "CELL_UPDATE_ACCEPTED": {

            console.log("UPDATE ACCEPTED!");

            gamePlayerStateCacheDispatcher(queryClient, gameId, userId, {
                type: "CELL_UPDATE_ACCEPTED",
                row: message.payload.row,
                col: message.payload.col,
                value: message.payload.value,
                currentStreak: message.payload.currentStreak
            });
            emitScoreUpdate(message.payload);
            emitTimerUpdate(message.payload);
            break;
        }
        case "CELL_UPDATE_REJECTED": {

            console.log("UPDATE REJECTED!");

            gamePlayerStateCacheDispatcher(queryClient, gameId, userId, {
                type: "CELL_UPDATE_REJECTED",
                row: message.payload.row,
                col: message.payload.col,
                value: message.payload.value,
                currentStreak: message.payload.currentStreak
            });
            emitScoreUpdate(message.payload);
            emitTimerUpdate(message.payload);
            break;
        }
        case "CELL_UPDATE_INVALID": {

            console.log("UPDATE INVALID!");

            // Add logging details
            break;
        }
        case "NOTE_UPDATE": {
            // IMPLEMENT - May not need a websocket event for this 
            break;
        }
        case "SETTINGS_UPDATED": {
            // IMPLEMENT
            break;
        }
    }
}

function emitScoreUpdate(payload: CellUpdatePayload) {
    if (payload.scoreUpdate && payload.scoreUpdate !== 0) {
        const signToAdd = payload.scoreUpdate > 0 ? "+" : "";
        notificationEmitter.emit({ type: "score", message: `${signToAdd}${payload.scoreUpdate} pts` });
    }
}

function emitTimerUpdate(payload: CellUpdatePayload) {
    if (payload.timerUpdate && payload.timerUpdate !== 0) {
        const signToAdd = payload.timerUpdate > 0 ? "+" : "";
        notificationEmitter.emit({ type: "timer", message: `${signToAdd}${payload.timerUpdate} secs` });
    }
}

type CellUpdatePayload = {
    row: number,
    col: number,
    value: number,
    scoreUpdate: number,
    timerUpdate?: number,
    currentStreak: number
}