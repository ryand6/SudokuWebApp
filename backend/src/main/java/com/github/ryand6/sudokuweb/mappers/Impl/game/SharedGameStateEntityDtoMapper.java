package com.github.ryand6.sudokuweb.mappers.Impl.game;

import com.github.ryand6.sudokuweb.domain.game.state.SharedGameStateEntity;
import com.github.ryand6.sudokuweb.dto.entity.game.SharedGameStateDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class SharedGameStateEntityDtoMapper implements EntityDtoMapper<SharedGameStateEntity, SharedGameStateDto> {

    @Override
    public SharedGameStateDto mapToDto(SharedGameStateEntity sharedGameState) {
        return SharedGameStateDto.builder()
                .cellFirstOwnership(sharedGameState.getCellFirstOwnership())
                .currentSharedBoardState(sharedGameState.getCurrentSharedBoardState())
                .build();
    }

}
