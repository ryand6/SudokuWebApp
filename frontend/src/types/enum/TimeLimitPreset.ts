export type TimeLimitPreset = "QUICK" | "STANDARD" | "MARATHON" | "UNLIMITED";

export const TimeLimitPresetInfo: Record<TimeLimitPreset, {
    displayName: string,
    seconds: number | null 
}> = {
    QUICK: {displayName: "Quick Game", seconds: 900},
    STANDARD: {displayName: "Standard", seconds: 1800},
    MARATHON: {displayName: "Marathon", seconds: 3600},
    UNLIMITED: {displayName: "No Time Limit", seconds: null},
}