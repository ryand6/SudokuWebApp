import type { InfiniteData } from "@tanstack/react-query"
import { PAGE_SIZE } from "../global/globalConstants"

type InfiniteDataEvent<T> = {
    type: string,
    newMessage: T
}

export function handleNewInfiniteData<T>(
    existingData: InfiniteData<T[]> | undefined, 
    event: InfiniteDataEvent<T>,
    getOrderKey?: (item: T) => number
): InfiniteData<T[], unknown> | undefined {

    const sortPage = (page: T[]) => 
        getOrderKey ? [...page].sort((a, b) => getOrderKey(b) - getOrderKey(a)) : page;

    if (!existingData || !existingData.pages[0]) {
        return {
            // first page
            pages: [[event.newMessage]],
            pageParams: [0]
        }
    }
    const firstPage = existingData.pages[0];
    if (firstPage && firstPage?.length >= PAGE_SIZE) {
        // Create a new page with message in it and insert at front of nested array 
        return {
            ...existingData,
            pages: [sortPage([event.newMessage]), ...existingData.pages],
            pageParams: [...existingData.pageParams, existingData.pageParams.length]
        };
    } else {
        // Add message to first page as this contains the most recent messages - add to start of page as the order will be reversed on render
        return {
            ...existingData,
            pages: [
                sortPage([event.newMessage, ...firstPage]),
                ...existingData.pages.slice(1),
            ],
        };
    }
}