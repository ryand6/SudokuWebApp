export type PreferenceDirection = "LESS" | "EQUAL" | "MORE";

export const PreferenceDirectionLabels: Record<PreferenceDirection, {
    generic: string,
    duration: string,
    difficulty: string
}> = {
    LESS : {generic: "Less", duration: "Shorter", difficulty: "Easier"},
    EQUAL: {generic: "Same", duration: "Same", difficulty: "Same"},
    MORE: {generic: "More", duration: "Longer", difficulty: "Harder"}
}