import type { UserDto } from "../dto/UserDto"


export type AuthContextValue = {
    user: UserDto | null,
    loadingUser: boolean,
    userFetchError: string | null,
    refreshUserAuth: () => Promise<void>,
    redirectPostLogin: () => void,
    logout: () => Promise<void>
}