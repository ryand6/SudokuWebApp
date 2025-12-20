import { getTenPublicLobbies } from "@/api/rest/lobby/getTenPublicLobbies";
import { useInfiniteQuery } from "@tanstack/react-query";

const PAGE_SIZE = 10;

export function useGetTenPublicLobbies() {
    return useInfiniteQuery({
        queryKey: ["publicLobbiesList"],
        queryFn: async ({ pageParam }) => {
            const dto = await getTenPublicLobbies(pageParam);
            return dto.publicLobbies;
        },
        initialPageParam: 0,
        // Pages use zero-based indexing, therefore next page is equal to the size of the the current page array (containing all pages retrieved so far) rather than incrementing the length by 1
        getNextPageParam: (lastPage, allPages) => lastPage.length === PAGE_SIZE ? allPages.length : undefined,
    })
}