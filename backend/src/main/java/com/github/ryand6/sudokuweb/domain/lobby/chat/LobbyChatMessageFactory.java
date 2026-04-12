package com.github.ryand6.sudokuweb.domain.lobby.chat;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.MessageType;

public class LobbyChatMessageFactory {

    public static LobbyChatMessageEntity createLobbyChatMessage(LobbyEntity lobby, UserEntity user, String message, MessageType messageType) {
        LobbyChatMessageEntity lobbyChatMessage = new LobbyChatMessageEntity();
        lobbyChatMessage.setLobbyEntity(lobby);
        lobbyChatMessage.setUserEntity(user);
        lobbyChatMessage.setMessage(message);
        lobbyChatMessage.setMessageType(messageType);
        return lobbyChatMessage;
    }

}
