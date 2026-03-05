package com.github.ryand6.sudokuweb.mappers.Impl.user;

import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserEntityDtoMapper implements EntityDtoMapper<UserEntity, UserDto> {

    private final UserStatsEntityDtoMapper userStatsEntityDtoMapper;
    private final UserSettingsEntityDtoMapper userSettingsEntityDtoMapper;

    public UserEntityDtoMapper(UserStatsEntityDtoMapper userStatsEntityDtoMapper, UserSettingsEntityDtoMapper userSettingsEntityDtoMapper) {
        this.userStatsEntityDtoMapper = userStatsEntityDtoMapper;
        this.userSettingsEntityDtoMapper = userSettingsEntityDtoMapper;
    }

    @Override
    public UserDto mapToDto(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .isOnline(userEntity.isOnline())
                .userStats(userStatsEntityDtoMapper.mapToDto(userEntity.getUserStatsEntity()))
                .userSettings(userSettingsEntityDtoMapper.mapToDto(userEntity.getUserSettingsEntity()))
                .build();
    }

}
