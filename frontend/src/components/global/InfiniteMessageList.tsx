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
            className="flex flex-col h-full overflow-y-auto"
            ref={chatRef}
            onScroll={onScroll}
        >
            <div ref={sentinelRef} className="h-1" />
            {messages.map((msg, index) => renderMessage(msg, index))}
            {hasNewMessages && !isAtBottom && (
                <Button
                    onClick={onScrollToBottom}
                    className="sticky bottom-3 right-3 ml-auto p-2 rounded-full shadow-lg transition cursor-pointer"
                    variant="default"
                >
                    <ChevronDown className="w-5 h-5" />
                </Button>
            )}
        </div>
    );
}