import type { UserDto } from "../../dto/UserDto"

export type AuthContextValue = {
    user: UserDto | null,
    loadingUser: boolean,
    userFetchError: string | null,
    refreshUser: () => Promise<void>,
    logout: () => Promise<void>
}