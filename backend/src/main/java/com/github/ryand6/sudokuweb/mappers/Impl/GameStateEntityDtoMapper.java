package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.GameStateDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.domain.game.GameRepository;
import com.github.ryand6.sudokuweb.domain.user.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class GameStateEntityDtoMapper implements EntityDtoMapper<GamePlayerStateEntity, GameStateDto> {

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
    public GameStateDto mapToDto(GamePlayerStateEntity gamePlayerStateEntity) {
        UserEntity user = gamePlayerStateEntity.getUserEntity();
        return GameStateDto.builder()
                .id(gamePlayerStateEntity.getId())
                .gameId(gamePlayerStateEntity.getGameEntity().getId())
                .user(userEntityDtoMapper.mapToDto(user))
                .score(gamePlayerStateEntity.getScore())
                .playerColour(gamePlayerStateEntity.getPlayerColour())
                .currentBoardState(gamePlayerStateEntity.getCurrentBoardState())
                .notes(gamePlayerStateEntity.getNotes())
                .build();
    }

}
