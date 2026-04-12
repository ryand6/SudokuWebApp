package com.github.ryand6.sudokuweb.mappers.Impl.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.chat.LobbyChatMessageEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class LobbyChatMessageEntityDtoMapper implements EntityDtoMapper<LobbyChatMessageEntity, LobbyChatMessageDto> {

    @Override
    public LobbyChatMessageDto mapToDto(LobbyChatMessageEntity lobbyChatMessageEntity) {
        UserEntity user = lobbyChatMessageEntity.getUserEntity();

        return LobbyChatMessageDto.builder()
                .id(lobbyChatMessageEntity.getId())
                .lobbyId(lobbyChatMessageEntity.getLobbyEntity().getId())
                .userId(user.getId())
                .username(user.getUsername())
                .message(lobbyChatMessageEntity.getMessage())
                .messageType(lobbyChatMessageEntity.getMessageType())
                .createdAt(lobbyChatMessageEntity.getCreatedAt())
                .build();
    }

}
