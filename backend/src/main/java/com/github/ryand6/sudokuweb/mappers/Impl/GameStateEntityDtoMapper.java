package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.GameStateEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.GameStateDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.GameRepository;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class GameStateEntityDtoMapper implements EntityDtoMapper<GameStateEntity, GameStateDto> {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final UserEntityDtoMapper userEntityDtoMapper;

    public GameStateEntityDtoMapper(GameRepository gameRepository,
                                    UserRepository userRepository,
                                    UserEntityDtoMapper userEntityDtoMapper) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.userEntityDtoMapper = userEntityDtoMapper;
    }

    @Override
    public GameStateDto mapToDto(GameStateEntity gameStateEntity) {
        UserEntity user = gameStateEntity.getUserEntity();
        return GameStateDto.builder()
                .id(gameStateEntity.getId())
                .gameId(gameStateEntity.getGameEntity().getId())
                .user(userEntityDtoMapper.mapToDto(user))
                .score(gameStateEntity.getScore())
                .playerColour(gameStateEntity.getPlayerColour())
                .currentBoardState(gameStateEntity.getCurrentBoardState())
                .notes(gameStateEntity.getNotes())
                .build();
    }

}
