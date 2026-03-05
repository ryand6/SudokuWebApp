import type { GamePlayerStateDto } from "@/types/dto/entity/game/GamePlayerStateDto";
import type { GamePlayerStateDtoRaw } from "@/types/dto/entity/game/GamePlayerStateDtoRaw";
import { decodeNotes } from "./noteEncoding";
import { CELL_COUNT } from "./gameConstants";

export function mapGameState(raw: GamePlayerStateDtoRaw): GamePlayerStateDto {

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