import { Button } from "../ui/button";
import { ChevronDown } from "lucide-react";
import type { ReactNode, RefObject } from "react";
import { ScrollArea } from "../ui/scroll-area";


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
        <ScrollArea className="flex-1 min-h-0 w-full">
            <div
                className="flex flex-col h-full"
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
        </ScrollArea>
        
    );
}