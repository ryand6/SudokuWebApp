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
    // Used to prevent the sentinel from loading all older messages when in view, prompting the load one page at a time until the chat is next scrolled to the top
    const [isLoadingNextPage, setIsLoadingNextPage] = useState(false);
    const sendLobbyChatMessage = useSendLobbyChatMessage();
    const chatRef = useRef<HTMLDivElement | null>(null);
    const [isAtBottom, setIsAtBottom] = useState(true);
    const [hasNewMessages, setHasNewMessages] = useState(false);
    // Used to store previous scroll height to help anchor position in chat window when older messages are loaded
    const scrollHeightRef = useRef(0);

    const { ref, inView } = useInView({
        // Call more page results as soon as the 'sentinel' div is in view
        threshold: 0,
    });

    useEffect(() => {
        // To prevent more pages being loaded on mount, isAtBottom is set to true to begin with
        if (!isAtBottom && inView && hasNextPage && !isFetchingNextPage && !isLoadingNextPage) {
            fetchNextPage();
            setIsLoadingNextPage(true);
        }
    }, [inView, hasNextPage, fetchNextPage, isFetchingNextPage]);

    const messages = useMemo(() => {
        // pages hold newest-first order - to render oldest->newest flatten reversed pages
        if (!chatMessages) return [];
        return chatMessages.pages.flat().reverse();
    }, [chatMessages]);

    // Ensures the chat shows persisted messages on refresh
    useEffect(() => {
        refetch(); 
    }, []);

    // On mount, scroll to bottom of chat
    useEffect(() => {
        scrollToBottom();
    }, []);

    useEffect(() => {
        const chatElement = chatRef.current;
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

    // Run when messages change
    useEffect(() => {
        const chatElement = chatRef.current;
        if (!chatElement) return;
        // If the user is not at bottom of chat window and new messages have been received, set flag
        if (!isAtBottom){
            setHasNewMessages(true);
        } else {
            // If near bottom, always scroll to bottom when a new message is received to keep updates real time
            scrollToBottom();
        }
    }, [messages])

    // Scroll handler to detect if user is near bottom of chat window
    const handleScroll = () => {
        const chatElement = chatRef.current;
        if (!chatElement) return;
        // If user is within 5px from bottom, declare that they are 'at the bottom' therefore new message icon can be removed
        const isNearBottom = (chatElement.clientHeight + chatElement.scrollTop >= chatElement.scrollHeight - 10);
        setIsAtBottom(isNearBottom);
        if (isNearBottom) setHasNewMessages(false);
    }

    // Button handler for scrolling to bottom, used for when new message icon is clicked
    const scrollToBottom = () => {
        const chatElement = chatRef.current;
        if (!chatElement) return;
        chatElement.scrollTo({ top: chatElement.scrollHeight, behavior: "smooth" })   
        setIsAtBottom(true);
    }

    const handleClick = () => {
        if (!inputMessage.trim()) return;
        sendLobbyChatMessage.mutate(
            {lobbyId: lobby.id, userId: currentUser.id, username: currentUser.username, message: inputMessage},
            {
                // Wait for message to be sent - fixes issue of scrolling to the bottom before new message is shown in DOM
                onSuccess: () => {
                    scrollToBottom();
                }
            }
        );
    }

    return (
        <div id="lobby-chat-panel" className="flex flex-col lobby-card flex-1 min-h-0">
            <h2 className="card-header">Lobby Chat</h2>
            <div id="lobby-chat-messages" className="overflow-y-auto flex flex-col flex-1 border-1 rounded-md my-2 min-h-0 max-h-[75vh]" ref={chatRef} onScroll={handleScroll}>
                <div ref={ref} className="h-1" />
                {messages?.map((msg, index) => {
                    if (!msg) return;
                    return (
                        <div id="lobby-message-container" className="flex flex-row m-2" key={index}>
                            <div id="lobby-message-user" className="w-[25%]">
                                {msg.username}
                            </div>
                            <div id="lobby-message-content" className="w-[65%]">
                                {msg.message}
                            </div>
                            <div id="lobby-message-timestamp" className="W-[10%]">
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