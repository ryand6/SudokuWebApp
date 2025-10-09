import { getTenPublicLobbies } from "@/api/lobby/getTenPublicLobbies";
import { useInfiniteQuery } from "@tanstack/react-query";

const PAGE_SIZE = 10;

export function useTenPublicLobbies() {
    return useInfiniteQuery({
        queryKey: ["publicLobbiesList"],
        queryFn: async ({ pageParam }) => {
            const dto = await getTenPublicLobbies(pageParam);
            return dto.publicLobbies;
        },
        initialPageParam: 0,
        getNextPageParam: (lastPage, allPages) => lastPage.length === PAGE_SIZE ? allPages.length + 1 : undefined,
    })
}