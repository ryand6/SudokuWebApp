import { useQuery } from "@tanstack/react-query";
import type { UserDto } from "../../../../types/dto/entity/user/UserDto";
import { getCurrentUser } from "./getCurrentUser";
import { queryKeys } from "@/state/queryKeys";

export function useGetCurrentUser() {
    return useQuery<UserDto, Error>({
        queryKey: queryKeys.user,
        queryFn: getCurrentUser,
        retry: false,
        // cache is updated by websocket connection, therefore refetching data not required
        staleTime: Infinity,
    });
}