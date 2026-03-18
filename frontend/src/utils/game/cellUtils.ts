import { sendPlayerHighlightedCellUpdate } from "@/api/ws/game/sendPlayerHighlightedCellUpdate";
import type { CellCoordinates } from "@/types/game/GameTypes";

export const playerColourClassNamePicker = {
  BLUE: {
    standard: "player-colour-blue",
    faded: "player-colour-blue-faded",
  },
  GREEN: {
    standard: "player-colour-green",
    faded: "player-colour-green-faded",
  },
  ORANGE: {
    standard: "player-colour-orange",
    faded: "player-colour-orange-faded",
  },
  PURPLE: {
    standard: "player-colour-purple",
    faded: "player-colour-purple-faded",
  },
};

export function updateGameHighlightedCellsAndSendWsUpdate(
    send: (dest: string, body: any) => void,
    gameId: number, 
    userId: number, 
    gameHighlightedCells: Map<number, CellCoordinates> | undefined, 
    cellCoordinates: CellCoordinates) {
    const updatedGameHighlightedCells = updateGameHighlightedCells(userId, gameHighlightedCells, cellCoordinates);
    sendPlayerHighlightedCellUpdate(send, gameId, userId, cellCoordinates.row, cellCoordinates.col);
    return updatedGameHighlightedCells;
}

export function updateGameHighlightedCells(
    userId: number, 
    gameHighlightedCells: Map<number, CellCoordinates> | undefined, 
    cellCoordinates: CellCoordinates
): Map<number, CellCoordinates> {
    if (!gameHighlightedCells) {
        console.log("RESETTING MAP");
        return new Map<number, CellCoordinates>([[userId, cellCoordinates]]);
    }
    const updatedGameHighlightedCells = new Map(gameHighlightedCells);
    updatedGameHighlightedCells.set(userId, cellCoordinates);
    return updatedGameHighlightedCells;
}