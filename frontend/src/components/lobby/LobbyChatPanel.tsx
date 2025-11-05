import { useLobbyMessages } from "@/hooks/lobby/useLobbyMessages"
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { Textarea } from "../ui/textarea";
import { Button } from "../ui/button";
import { useState } from "react";
import { useSendLobbyChatMessage } from "@/hooks/lobby/useSendLobbyChatMessage";
import type { UserDto } from "@/types/dto/entity/UserDto";

export function LobbyChatPanel({lobby, currentUser}: {lobby: LobbyDto, currentUser: UserDto}) {

    const {data: messages} = useLobbyMessages(lobby.id);
    const [inputMessage, setInputMessage] = useState("");
    const sendLobbyChatMessage = useSendLobbyChatMessage();

    const handleClick = () => {
        if (!inputMessage.trim()) return;
        console.log(messages);
        sendLobbyChatMessage.mutate({lobbyId: lobby.id, userId: currentUser.id, username: currentUser.username, message: inputMessage});
    }

    return (
        <div id="lobby-chat-panel">
            <h2>Lobby Chat</h2>
            <div id="lobby-chat-messages" className="overflow-scroll">
                {messages?.map((msg, index) => {
                    return (
                        <div id="lobby-message-container" key={index}>
                            <div id="lobby-message-user">
                                {msg.username}
                            </div>
                            <div id="lobby-message-content">
                                {msg.message}
                            </div>
                        </div>
                    )
                })}
            </div>
            <div>
                <Textarea 
                    id="lobby-chat-input" 
                    placeholder="Type your message here." 
                    onChange={(e) => setInputMessage(e.target.value)}
                />
                <Button onClick={handleClick}>Send message</Button>
            </div>
        </div>
    )
}