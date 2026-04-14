import type { Theme } from "@/types/enum/Theme"

export type UserSettingsDto = {
    theme: Theme,
    opponentHighlightedSquaresEnabled: boolean,
    highlightedHousesEnabled: boolean,
    highlightedFirstsEnabled: boolean,
    audioEnabled: boolean,
    gameChatNotificationsEnabled: boolean,
    scoreNotificationsEnabled: boolean,
    streakNotificationsEnabled: boolean
}