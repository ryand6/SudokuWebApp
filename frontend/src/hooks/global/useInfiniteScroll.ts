// hooks/shared/useInfiniteScroll.ts
import { useCallback, useEffect, useRef, useState } from "react";


export function useInfiniteScroll<T>({ 
    isFetchingNextPage, 
    messages, 
    isAtBottom, 
    setIsAtBottom, 
    setIsLoadingNextPage 
}: {
    isFetchingNextPage: boolean;
    messages: T[];
    isAtBottom: boolean;
    setIsAtBottom: (value: boolean) => void;
    setIsLoadingNextPage: (value: boolean) => void;
}) {

    const chatRef = useRef<HTMLDivElement | null>(null);
    const scrollHeightRef = useRef(0);
    const [hasNewMessages, setHasNewMessages] = useState(false);

    const scrollToBottom = useCallback(() => {
        const chatElement = chatRef.current;
        if (!chatElement) return;
        chatElement.scrollTo({ top: chatElement.scrollHeight, behavior: "smooth" });
        setIsAtBottom(true);
    }, [chatRef, setIsAtBottom]);

    useEffect(() => { 
        scrollToBottom(); 
    }, [scrollToBottom]);

    useEffect(() => {
        const chatElement = chatRef.current;
        if (!chatElement) return;
        if (isFetchingNextPage) {
            scrollHeightRef.current = chatElement.scrollHeight;
        } else {
            chatElement.scrollTop = chatElement.scrollHeight - scrollHeightRef.current;
            setIsLoadingNextPage(false);
        }
    }, [isFetchingNextPage, setIsLoadingNextPage]);

    useEffect(() => {
        if (!chatRef.current) return;
        if (!isAtBottom) {
            setHasNewMessages(true);
        } else {
            scrollToBottom();
        }
    }, [messages]);

    const handleScroll = () => {
        const chatElement = chatRef.current;
        if (!chatElement) return;
        const isNearBottom = chatElement.clientHeight + chatElement.scrollTop >= chatElement.scrollHeight - 10;
        setIsAtBottom(isNearBottom);
        if (isNearBottom) setHasNewMessages(false);
    };

    return { chatRef, hasNewMessages, scrollToBottom, handleScroll };
}