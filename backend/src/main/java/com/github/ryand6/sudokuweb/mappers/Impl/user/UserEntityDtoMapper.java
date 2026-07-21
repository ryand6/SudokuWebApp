package com.github.ryand6.sudokuweb.mappers.Impl.user;

import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserEntityDtoMapper implements EntityDtoMapper<UserEntity, UserDto> {

    private final UserSettingsEntityDtoMapper userSettingsEntityDtoMapper;

    public UserEntityDtoMapper(UserSettingsEntityDtoMapper userSettingsEntityDtoMapper) {
        this.userSettingsEntityDtoMapper = userSettingsEntityDtoMapper;
    }

    @Override
    public UserDto mapToDto(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .isOnline(userEntity.isOnline())
                .userSettings(userSettingsEntityDtoMapper.mapToDto(userEntity.getUserSettingsEntity()))
                .build();
    }

}
