import { useEffect, type RefObject, type SetStateAction } from "react";

export function useHandleScrollOnMessagePageLoad(
    chatElement: HTMLDivElement | null,
    isFetchingNextPage: boolean,
    scrollHeightRef: RefObject<number>,
    setIsLoadingNextPage: (value: SetStateAction<boolean>) => void
) {
    // custom hook used to track and position scroll heights when pages are fetched to create a seamless scroll when new pages load - prevents scroll height jumping when pages are fetched
    useEffect(() => {
        if (!chatElement) return;
        // Store previous scroll height whilst next page of messages is being fetched 
        if (isFetchingNextPage) {
            scrollHeightRef.current = chatElement.scrollHeight;
        } else { 
            // Once next page of messages has been fetched, anchor the chat window position by setting the distance scrolled to the difference between the current and old previous scrollHeight 
            chatElement.scrollTop = chatElement.scrollHeight - scrollHeightRef.current;
            // Remove flag so that next page of messages can be loaded once the window is scrolled to the top again
            setIsLoadingNextPage(false);
        }
    }, [isFetchingNextPage])
}