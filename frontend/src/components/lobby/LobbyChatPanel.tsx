import { useGetLobbyChatMessages } from "@/hooks/lobby/useGetLobbyChatMessages"
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { Textarea } from "../ui/textarea";
import { Button } from "../ui/button";
import { useEffect, useMemo, useRef, useState } from "react";
import type { UserDto } from "@/types/dto/entity/UserDto";
import { useInView } from "react-intersection-observer";
import { ChevronDown } from "lucide-react";
import { getLocalTime } from "@/utils/time/getLocalTime";
import { sendLobbyChatMessage } from "@/api/ws/lobby/sendLobbyChatMessage";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { useHandleFetchNextLobbyMessagePage } from "@/hooks/lobby/useHandleFetchNextLobbyMessagePage";
import { useHandleScrollOnMessagePageLoad } from "@/hooks/lobby/useHandleScrollOnMessagePageLoad";
import { useHandleNewChatMessages } from "@/hooks/lobby/useHandleNewChatMessages";

export function LobbyChatPanel({lobby, currentUser}: {lobby: LobbyDto, currentUser: UserDto}) {

    const { send } = useWebSocketContext();

    const {data: chatMessages, isLoading, isError, error, hasNextPage, fetchNextPage, isFetchingNextPage, refetch } = useGetLobbyChatMessages(lobby.id);
    const [inputMessage, setInputMessage] = useState("");
    // Used to prevent the sentinel from loading all older messages when in view, prompting the load one page at a time until the chat is next scrolled to the top
    const [isLoadingNextPage, setIsLoadingNextPage] = useState(false);
    const chatRef = useRef<HTMLDivElement | null>(null);
    const [isAtBottom, setIsAtBottom] = useState(true);
    const [hasNewMessages, setHasNewMessages] = useState(false);
    // Used to store previous scroll height to help anchor position in chat window when older messages are loaded
    const scrollHeightRef = useRef(0);

    const { ref, inView } = useInView({
        // Call more page results as soon as the 'sentinel' div is in view
        threshold: 0,
    });

    // custom hook used to handle fetching next page of lobby messages
    useHandleFetchNextLobbyMessagePage(isAtBottom, inView, hasNextPage, isFetchingNextPage, isLoadingNextPage, fetchNextPage, setIsLoadingNextPage);

    const messages = useMemo(() => {
        // pages hold newest-first order - to render oldest->newest flatten reversed pages
        if (!chatMessages) return [];
        return chatMessages.pages.flat().reverse();
    }, [chatMessages]);

    // Button handler for scrolling to bottom, used for when new message icon is clicked
    const scrollToBottom = () => {
        const chatElement = chatRef.current;
        if (!chatElement) return;
        chatElement.scrollTo({ top: chatElement.scrollHeight, behavior: "smooth" })   
        setIsAtBottom(true);
    };

    // Ensures the chat shows persisted messages on refresh
    useEffect(() => {
        refetch(); 
    }, []);

    // On mount, scroll to bottom of chat
    useEffect(() => {
        scrollToBottom();
    }, []);

    // custom hook used to track and position scroll heights when pages are fetched to create a seamless scroll when new pages load - prevents scroll height jumping when pages are fetched
    useHandleScrollOnMessagePageLoad(chatRef.current, isFetchingNextPage, scrollHeightRef, setIsLoadingNextPage);

    // custom hook to either jump to bottom of chat window when near it and new messages are received, or display icon that indicates new messages are received
    useHandleNewChatMessages(chatRef.current, isAtBottom, setHasNewMessages, scrollToBottom, messages);

    // Scroll handler to detect if user is near bottom of chat window
    const handleScroll = () => {
        const chatElement = chatRef.current;
        if (!chatElement) return;
        // If user is within 5px from bottom, declare that they are 'at the bottom' therefore new message icon can be removed
        const isNearBottom = (chatElement.clientHeight + chatElement.scrollTop >= chatElement.scrollHeight - 10);
        setIsAtBottom(isNearBottom);
        if (isNearBottom) setHasNewMessages(false);
    };

    const handleClick = () => {
        if (!inputMessage.trim()) return;
        sendLobbyChatMessage(send, lobby.id, currentUser.id, inputMessage);
        // Clear text area 
        setInputMessage("");
        scrollToBottom();
    };

    return (
        <div id="lobby-chat-panel" className="flex flex-col lobby-card flex-1 min-h-0">
            <h2 className="card-header">Lobby Chat</h2>
            <div id="lobby-chat-messages" className="overflow-y-auto flex flex-col flex-1 border-1 rounded-md my-2 min-h-0 max-h-[75vh]" ref={chatRef} onScroll={handleScroll}>
                <div ref={ref} className="h-1" />
                {messages?.map((msg, index) => {
                    if (!msg) return;
                    switch (msg.messageType) {
                        case "MESSAGE":
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
                                );
                        case "INFO": 
                            return (
                                <div id="lobby-info-message-container" className="flex flex-row m-1 text-sm" key={index}>
                                    <div id="lobby-info-message-content" className="w-[90%]">
                                        <span>{msg.username} </span>
                                        <span>{msg.message}</span>
                                    </div>
                                    <div id="lobby-info-message-timestamp" className="W-[10%]">
                                        {getLocalTime(msg.createdAt)}
                                    </div>
                                </div>
                            )
                    }
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
                    value={inputMessage} 
                    onChange={(e) => {
                        setInputMessage(e.target.value);
                    }}
                    onKeyDown={(e) => {
                        if (e.key === "Enter" && !e.shiftKey) {
                            e.preventDefault();
                            handleClick();
                        }
                    }}
                />
                <Button onClick={handleClick}>Send message</Button>
            </div>
        </div>
    )
}