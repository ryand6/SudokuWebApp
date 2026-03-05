import { useQuery } from "@tanstack/react-query";
import type { UserDto } from "../../../../types/dto/entity/user/UserDto";
import { getCurrentUser } from "./getCurrentUser";

export function useGetCurrentUser() {
    return useQuery<UserDto, Error>({
        queryKey: ["currentUser"],
        queryFn: getCurrentUser,
        retry: false,
        // cache is updated by websocket connection, therefore refetching data not required
        staleTime: Infinity,
    });
}