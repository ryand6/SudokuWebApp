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
        // Pages use zero-based indexing, therefore next page is equal to the size of the length of the current page array rather than incrementing the length by 1
        getNextPageParam: (lastPage, allPages) => lastPage.length === PAGE_SIZE ? allPages.length : undefined,
    })
}