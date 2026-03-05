import type { UserSettingsDto } from "./UserSettingsDto"
import type { UserStatsDto } from "./UserStatsDto"

export type UserDto = {
    id: number,
    username: string,
    isOnline: boolean,
    userStats: UserStatsDto
    userSettings: UserSettingsDto
}