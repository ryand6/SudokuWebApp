package com.github.ryand6.sudokuweb.mappers.Impl.game;

import com.github.ryand6.sudokuweb.domain.game.chat.GameChatMessageEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.game.GameChatMessageDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class GameChatMessageEntityDtoMapper implements EntityDtoMapper<GameChatMessageEntity, GameChatMessageDto> {

    @Override
    public GameChatMessageDto mapToDto(GameChatMessageEntity gameChatMessage) {
        UserEntity user = gameChatMessage.getUserEntity();

        return GameChatMessageDto.builder()
                .id(gameChatMessage.getId())
                .gameId(gameChatMessage.getGameEntity().getId())
                .userId(user.getId())
                .username(user.getUsername())
                .message(gameChatMessage.getMessage())
                .messageType(gameChatMessage.getMessageType())
                .createdAt(gameChatMessage.getCreatedAt())
                .build();
    }

}
