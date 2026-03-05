package com.github.ryand6.sudokuweb.mappers.Impl.game;

import com.github.ryand6.sudokuweb.domain.game.event.GameEventEntity;
import com.github.ryand6.sudokuweb.dto.entity.game.GameEventDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
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
