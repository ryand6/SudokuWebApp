package com.github.ryand6.sudokuweb.domain.game.chat;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.MessageType;

public class GameChatMessageFactory {

    public static GameChatMessageEntity createGameChatMessage(GameEntity gameEntity, UserEntity userEntity, String message, MessageType messageType) {
        GameChatMessageEntity gameChatMessage = new GameChatMessageEntity();
        gameChatMessage.setGameEntity(gameEntity);
        gameChatMessage.setUserEntity(userEntity);
        gameChatMessage.setMessage(message);
        gameChatMessage.setMessageType(messageType);
        return gameChatMessage;
    }

}
