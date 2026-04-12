import type { PlayerColour } from "@/types/enum/PlayerColour";

export const playerColourClassNamePicker = {
  ONE: {
    strong: "player-colour-one-strong",
    medium: "player-colour-one-medium",
    light: "player-colour-one-light",
    hover: "hover-player-colour-one",
    shine: "shine-one"
  },
  TWO: {
    strong: "player-colour-two-strong",
    medium: "player-colour-two-medium",
    light: "player-colour-two-light",
    hover: "hover-player-colour-two",
    shine: "shine-two"
  },
  THREE: {
    strong: "player-colour-three-strong",
    medium: "player-colour-three-medium",
    light: "player-colour-three-light",
    hover: "hover-player-colour-three",
    shine: "shine-three"
  },
  FOUR: {
    strong: "player-colour-four-strong",
    medium: "player-colour-four-medium",
    light: "player-colour-four-light",
    hover: "hover-player-colour-four",
    shine: "shine-four"
  },
};

export const toastColourMap: Record<PlayerColour, string> = {
    ONE:   "var(--player-colour-one-light)",
    TWO:   "var(--player-colour-two-light)",
    THREE: "var(--player-colour-three-light)",
    FOUR:  "var(--player-colour-four-light)",
};