import { sendPlayerHighlightedCellUpdate } from "@/api/ws/game/sendPlayerHighlightedCellUpdate";
import type { CellCoordinates } from "@/types/game/GameTypes";

export const playerColourClassNamePicker = {
  BLUE: {
    strong: "player-colour-blue-strong",
    medium: "player-colour-blue-medium",
    light: "player-colour-blue-light",
    hover: "hover-player-colour-blue",
    shine: "shine-blue"
  },
  GREEN: {
    strong: "player-colour-green-strong",
    medium: "player-colour-green-medium",
    light: "player-colour-green-light",
    hover: "hover-player-colour-green",
    shine: "shine-green"
  },
  RED: {
    strong: "player-colour-red-strong",
    medium: "player-colour-red-medium",
    light: "player-colour-red-light",
    hover: "hover-player-colour-red",
    shine: "shine-red"
  },
  PURPLE: {
    strong: "player-colour-purple-strong",
    medium: "player-colour-purple-medium",
    light: "player-colour-purple-light",
    hover: "hover-player-colour-purple",
    shine: "shine-purple"
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

const CORNER_POSITIONS: Record<number, string> = {
    0: "-top-2 -left-2",
    1: "-top-2 -right-2", 
    2: "-bottom-2 -right-2",
    3: "-bottom-2 -left-2",
}

export function getPlayerCorner(playerCornerIndex: number): string {
    return CORNER_POSITIONS[playerCornerIndex];
}