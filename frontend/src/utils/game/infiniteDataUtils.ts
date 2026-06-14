import type { InfiniteData } from "@tanstack/react-query"
import { PAGE_SIZE } from "../global/globalConstants"
import type { MessageType } from "@/types/enum/MessageType"
import { getLocalTime } from "../time/getLocalTime"

type InfiniteDataEvent<T> = {
    type: string,
    newMessage: T
}

type ChatMessage = {
    id: number,
    userId: number,
    username: string, 
    message: string,
    messageType: MessageType;
    createdAt: string;
}

export type GroupedMessage = {
    id: number,
    message: string,
    createdAt: string,
    showTimestamp: boolean
}

export type ChatMessageGroup = {
    userId: number,
    username: string,
    messageType: MessageType,
    messages: GroupedMessage[]
}

export function handleNewInfiniteData<T>(
    existingData: InfiniteData<T[]> | undefined, 
    event: InfiniteDataEvent<T>,
    getOrderKey?: (item: T) => number,
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


export function groupMessages<T extends ChatMessage>(
    messages: T[] | undefined
): ChatMessageGroup[] {
    if (!messages) return [];

    const groups: ChatMessageGroup[] = [];

    for (const message of messages) {
        if (message.messageType === "INFO") {
            groups.push({
                userId: message.userId,
                username: message.username,
                messageType: message.messageType,
                messages: [{
                    id: message.id,
                    message: message.message,
                    createdAt: message.createdAt,
                    showTimestamp: true
                }]
            })
            continue;
        }

        const lastGroup = groups[groups.length - 1];
        const lastMessage = lastGroup?.messages[lastGroup.messages.length - 1];

        const sameUser = lastGroup?.userId === message.userId;
        const sameTime = lastMessage ? getLocalTime(lastMessage.createdAt) === getLocalTime(message.createdAt) : false;
        const sameGroupType = lastGroup?.messageType !== "INFO";

        if (sameUser && sameTime && sameGroupType) {
            lastGroup.messages.push({
                id: message.id,
                message: message.message,
                createdAt: message.createdAt,
                showTimestamp: false
            })
        } else if (sameUser && !sameTime && sameGroupType) {
            lastGroup.messages.push({
                id: message.id,
                message: message.message,
                createdAt: message.createdAt,
                showTimestamp: true
            })
        } else {
            // Different user — start a new group, always show timestamp
            groups.push({
                userId: message.userId,
                username: message.username,
                messageType: message.messageType,
                messages: [{
                    id: message.id,
                    message: message.message,
                    createdAt: message.createdAt,
                    showTimestamp: true
                }],
            });
        }
    }
    return groups;
}