import type { QueryClient } from "@tanstack/react-query";
import { queryKeys } from "../queryKeys";
import type { UserDto } from "@/types/dto/entity/user/UserDto";
import type { UserEvent } from "./userEvents";
import { userCacheReducer } from "./userCacheReducer";

export function userCacheDispatcher(
    queryClient: QueryClient,
    event: UserEvent
) {
    queryClient.setQueryData<UserDto>(queryKeys.user, (old: UserDto | undefined) => {
        return userCacheReducer(old, event);
    })
}