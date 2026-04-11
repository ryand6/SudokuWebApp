import { Button } from "../ui/button";
import { ChevronDown } from "lucide-react";
import type { ReactNode, RefObject } from "react";


export function InfiniteMessageList<T>({ 
    chatRef, 
    sentinelRef, 
    hasNewMessages, 
    isAtBottom, 
    onScroll, 
    onScrollToBottom, 
    messages, 
    renderMessage 
}: {
    chatRef: RefObject<HTMLDivElement | null>;
    sentinelRef: (node?: Element | null) => void;
    hasNewMessages: boolean;
    isAtBottom: boolean;
    onScroll: () => void;
    onScrollToBottom: () => void;
    messages: T[];
    renderMessage: (message: T, index: number) => ReactNode;
}) {
    return (
        <div
            className="overflow-y-auto flex flex-col flex-1 border-1 rounded-md my-2 min-h-0 max-h-[75vh]"
            ref={chatRef}
            onScroll={onScroll}
        >
            <div ref={sentinelRef} className="h-1" />
            {messages.map((msg, index) => renderMessage(msg, index))}
            {hasNewMessages && !isAtBottom && (
                <Button
                    onClick={onScrollToBottom}
                    className="absolute bottom-3 right-3 p-2 rounded-full shadow-lg transition cursor-pointer"
                    variant="secondary"
                >
                    <ChevronDown className="w-5 h-5" />
                </Button>
            )}
        </div>
    );
}