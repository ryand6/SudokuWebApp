import type { PrivateGamePlayerStateDto } from "@/types/dto/entity/game/PrivateGamePlayerStateDto";
import type { PrivateGamePlayerStateDtoRaw } from "@/types/dto/entity/game/PrivateGamePlayerStateDtoRaw";
import { decodeNotes } from "./noteEncoding";
import { CELL_COUNT } from "./gameConstants";

export function mapGameState(raw: PrivateGamePlayerStateDtoRaw): PrivateGamePlayerStateDto {

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