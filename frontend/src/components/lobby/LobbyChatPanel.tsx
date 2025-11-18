import { useGetLobbyChatMessages } from "@/hooks/lobby/useGetLobbyChatMessages"
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { Textarea } from "../ui/textarea";
import { Button } from "../ui/button";
import { useEffect, useMemo, useRef, useState } from "react";
import { useSendLobbyChatMessage } from "@/hooks/lobby/useSendLobbyChatMessage";
import type { UserDto } from "@/types/dto/entity/UserDto";
import { useInView } from "react-intersection-observer";
import { ChevronDown } from "lucide-react";
import { getLocalTime } from "@/utils/time/getLocalTime";

export function LobbyChatPanel({lobby, currentUser}: {lobby: LobbyDto, currentUser: UserDto}) {

    const {data: chatMessages, isLoading, isError, error, hasNextPage, fetchNextPage, isFetchingNextPage, refetch } = useGetLobbyChatMessages(lobby.id);
    const [inputMessage, setInputMessage] = useState("");
    const sendLobbyChatMessage = useSendLobbyChatMessage();
    const chatRef = useRef<HTMLDivElement | null>(null);
    const [isAtBottom, setIsAtBottom] = useState(true);
    const [hasNewMessages, setHasNewMessages] = useState(false);

    const { ref, inView } = useInView({
        // Call more page results as soon as the 'sentinel' div is in view
        threshold: 0,
    });

    useEffect(() => {
        if (inView && hasNextPage && !isFetchingNextPage) {
            fetchNextPage();
        }
    }, [inView, hasNextPage, fetchNextPage, isFetchingNextPage]);

    // Ensures the chat shows persisted messages on refresh
    useEffect(() => {
        refetch(); 
    }, []);

    // On mount, scroll to bottom of chat
    useEffect(() => {
        scrollToBottom();
    }, []);

    // Run when messages change
    useEffect(() => {
        const chatElement = chatRef.current;
        if (!chatElement) return;
        // If the user is not at bottom of chat window and new messages have been received, set flag
        if (!isAtBottom) setHasNewMessages(true);
    }, [chatMessages])

    // Scroll handler to detect if user is near bottom of chat window
    const handleScroll = () => {
        const chatElement = chatRef.current;
        if (!chatElement) return;
        // If user is within 50px from bottom, declare that they are 'at the bottom' therefore new message icon can be removed
        const isNearBottom = (chatElement.scrollHeight - chatElement.clientHeight - chatElement.scrollTop) > 50;
        setIsAtBottom(isNearBottom);
        if (isNearBottom) setHasNewMessages(false);
    }

    // Button handler for scrolling to bottom, used for when new message icon is clicked
    const scrollToBottom = () => {
        const chatElement = chatRef.current;
        if (!chatElement) return;
        const scrollTopPx = chatElement.scrollHeight - chatElement.clientHeight;
        chatElement.scrollTo({ top: scrollTopPx, behavior: "smooth" })
    }

    const handleClick = () => {
        if (!inputMessage.trim()) return;
        sendLobbyChatMessage.mutate({lobbyId: lobby.id, userId: currentUser.id, username: currentUser.username, message: inputMessage});
    }

    const messages = useMemo(() => {
        // pages hold newest-first order - to render oldest->newest flatten reversed pages
        if (!chatMessages) return [];
        return chatMessages.pages.flat().reverse();
    }, [chatMessages]);

    console.log(messages);

    return (
        <div id="lobby-chat-panel">
            <h2>Lobby Chat</h2>
            <div id="lobby-chat-messages" className="overflow-y-auto" ref={chatRef} onScroll={handleScroll}>
                <div ref={ref} className="h-5" />
                {messages?.map((msg, index) => {
                    if (!msg) return;
                    return (
                        <div id="lobby-message-container" key={index}>
                            <div id="lobby-message-user">
                                {msg.username}
                            </div>
                            <div id="lobby-message-content">
                                {msg.message}
                            </div>
                            <div id="lobby-message-timestamp">
                                {getLocalTime(msg.createdAt)}
                            </div>
                        </div>
                    )
                })}
                {/* "Scroll to bottom" indicator */}
                {hasNewMessages && !isAtBottom && (
                    <button
                        onClick={scrollToBottom}
                        className="absolute bottom-3 right-3 bg-blue-500 hover:bg-blue-600 text-white p-2 rounded-full shadow-lg transition"
                    >
                        <ChevronDown className="w-5 h-5" />
                    </button>
                )}
            </div>
            <div>
                <Textarea 
                    id="lobby-chat-input" 
                    placeholder="Type your message here." 
                    onChange={(e) => {
                        e.preventDefault();
                        setInputMessage(e.target.value);
                    }}
                />
                <Button onClick={handleClick}>Send message</Button>
            </div>
        </div>
    )
}