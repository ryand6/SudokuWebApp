// hooks/shared/useInfiniteScroll.ts
import { useEffect, useRef, useState } from "react";


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

    useEffect(() => { 
        scrollToBottom(); 
    }, []);

    useEffect(() => {
        const chatElement = chatRef.current;
        if (!chatElement) return;
        if (isFetchingNextPage) {
            scrollHeightRef.current = chatElement.scrollHeight;
        } else {
            chatElement.scrollTop = chatElement.scrollHeight - scrollHeightRef.current;
            setIsLoadingNextPage(false);
        }
    }, [isFetchingNextPage]);

    useEffect(() => {
        if (!chatRef.current) return;
        if (!isAtBottom) {
            setHasNewMessages(true);
        } else {
            scrollToBottom();
        }
    }, [messages]);

    const scrollToBottom = () => {
        const chatElement = chatRef.current;
        if (!chatElement) return;
        chatElement.scrollTo({ top: chatElement.scrollHeight, behavior: "smooth" });
        setIsAtBottom(true);
    };

    const handleScroll = () => {
        const chatElement = chatRef.current;
        if (!chatElement) return;
        const isNearBottom = chatElement.clientHeight + chatElement.scrollTop >= chatElement.scrollHeight - 10;
        setIsAtBottom(isNearBottom);
        if (isNearBottom) setHasNewMessages(false);
    };

    return { chatRef, hasNewMessages, scrollToBottom, handleScroll };
}