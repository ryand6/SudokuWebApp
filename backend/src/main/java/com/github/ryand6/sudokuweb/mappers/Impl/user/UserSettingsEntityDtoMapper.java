package com.github.ryand6.sudokuweb.mappers.Impl.user;

import com.github.ryand6.sudokuweb.domain.user.settings.UserSettingsEntity;
import com.github.ryand6.sudokuweb.dto.entity.user.UserSettingsDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserSettingsEntityDtoMapper implements EntityDtoMapper<UserSettingsEntity, UserSettingsDto> {

    @Override
    public UserSettingsDto mapToDto(UserSettingsEntity userSettings) {
        return UserSettingsDto.builder()
                .isDarkModeActive(userSettings.isDarkModeActive())
                .isSoundActive(userSettings.isSoundActive())
                .build();
    }

}
