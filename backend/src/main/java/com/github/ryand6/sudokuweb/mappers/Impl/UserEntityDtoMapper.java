package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.UserDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserEntityDtoMapper implements EntityDtoMapper<UserEntity, UserDto> {

    private final ScoreEntityDtoMapper scoreEntityDtoMapper;

    public UserEntityDtoMapper(ScoreEntityDtoMapper scoreEntityDtoMapper) {
        this.scoreEntityDtoMapper = scoreEntityDtoMapper;
    }


    @Override
    public UserDto mapToDto(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .isOnline(userEntity.getIsOnline())
                .score(scoreEntityDtoMapper.mapToDto(userEntity.getScoreEntity()))
                .build();
    }

}
