import { sendPlayerHighlightedCellUpdate } from "@/api/ws/game/sendPlayerHighlightedCellUpdate";
import type { CellCoordinates } from "@/types/game/GameTypes";

export const playerColourClassNamePicker = {
  BLUE: {
    strong: "player-colour-blue-strong",
    medium: "player-colour-blue-medium",
    light: "player-colour-blue-light",
  },
  GREEN: {
    strong: "player-colour-green-strong",
    medium: "player-colour-green-medium",
    light: "player-colour-green-light",
  },
  ORANGE: {
    strong: "player-colour-orange-strong",
    medium: "player-colour-orange-medium",
    light: "player-colour-orange-light",
  },
  PURPLE: {
    strong: "player-colour-purple-strong",
    medium: "player-colour-purple-medium",
    light: "player-colour-purple-light",
  },
};

export function getCellIndex(row: number, col: number) {
  return (row * 9) + col;
}

export function updateGameHighlightedCellsAndSendWsUpdate(
  send: (dest: string, body: any) => void,
  gameId: number, 
  userId: number, 
  gameHighlightedCells: Map<number, CellCoordinates> | undefined, 
  cellCoordinates: CellCoordinates
) {
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
    return new Map<number, CellCoordinates>([[userId, cellCoordinates]]);
  }
  const updatedGameHighlightedCells = new Map(gameHighlightedCells);
  updatedGameHighlightedCells.set(userId, cellCoordinates);
  return updatedGameHighlightedCells;
}

export function onHoverHandler(setIsHovered: React.Dispatch<React.SetStateAction<boolean>>) {
  return () => setIsHovered(true);
}

export function onLeaveHandler(setIsHovered: React.Dispatch<React.SetStateAction<boolean>>) {
  return () => setIsHovered(false);
}