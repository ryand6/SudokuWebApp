// export type GameMode = "CLASSIC" | "DOMINATION" | "TIMEATTACK";

export const gameModes = ["CLASSIC", "DOMINATION", "TIMEATTACK"] as const;

export type GameMode = typeof gameModes[number]; 