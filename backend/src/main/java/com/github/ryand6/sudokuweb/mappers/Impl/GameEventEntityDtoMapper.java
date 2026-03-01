package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.game.GameEventEntity;
import com.github.ryand6.sudokuweb.dto.entity.GameEventDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;

public class GameEventEntityDtoMapper implements EntityDtoMapper<GameEventEntity, GameEventDto> {

    @Override
    public GameEventDto mapToDto(GameEventEntity gameEventEntity) {
        return GameEventDto.builder()
                .gameId(gameEventEntity.getGameEntity().getId())
                .userId(gameEventEntity.getUserEntity().getId())
                .eventType(gameEventEntity.getEventType())
                .payload(gameEventEntity.getPayload())
                .sequenceNumber(gameEventEntity.getSequenceNumber())
                .build();
    }

}
