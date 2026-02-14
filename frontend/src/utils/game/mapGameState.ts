import type { GameStateDto } from "@/types/dto/entity/GameStateDto";
import type { GameStateDtoRaw } from "@/types/dto/entity/GameStateDtoRaw";
import { decodeNotes } from "./noteEncoding";
import { CELL_COUNT } from "./gameConstants";

export function mapGameState(raw: GameStateDtoRaw): GameStateDto {

  let notes: Uint16Array = new Uint16Array(CELL_COUNT);

  try {
    notes = decodeNotes(raw.notes);
  } catch (error) {
    console.log(`Error decoding notes: `, error);
  }

  return {
    ...raw,
    notes: notes,
  };
}