import type { UserSettingsDto } from "./UserSettingsDto"

export type UserDto = {
    id: number,
    username: string,
    isOnline: boolean,
    userSettings: UserSettingsDto
}