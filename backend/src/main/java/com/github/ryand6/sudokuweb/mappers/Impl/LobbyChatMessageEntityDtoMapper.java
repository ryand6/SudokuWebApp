package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.LobbyChatMessageEntity;
import com.github.ryand6.sudokuweb.dto.entity.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class LobbyChatMessageEntityDtoMapper implements EntityDtoMapper<LobbyChatMessageEntity, LobbyChatMessageDto> {

    @Override
    public LobbyChatMessageDto mapToDto(LobbyChatMessageEntity lobbyChatMessageEntity) {
        return LobbyChatMessageDto.builder()
                .id(lobbyChatMessageEntity.getId())
                .lobbyId(lobbyChatMessageEntity.getLobbyEntity().getId())
                .username(lobbyChatMessageEntity.getUserEntity().getUsername())
                .message(lobbyChatMessageEntity.getMessage())
                .createdAt(lobbyChatMessageEntity.getCreatedAt())
                .build();
    }

}
