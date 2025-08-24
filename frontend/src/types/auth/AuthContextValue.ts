import type { UserDto } from "../../dto/UserDto"

export type AuthContextValue = {
    user: UserDto | null,
    loadingUser: boolean,
    userFetchError: string | null,
    userSetupRequired: boolean,
    loginRequired: boolean,
    refreshUser: () => Promise<void>,
    logout: () => Promise<void>
}