package com.github.ryand6.sudokuweb.services.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.ryand6.sudokuweb.domain.user.settings.UserSettingsEntity;
import com.github.ryand6.sudokuweb.domain.user.settings.UserSettingsRepository;
import com.github.ryand6.sudokuweb.dto.request.SingleFieldPatch;
import com.github.ryand6.sudokuweb.exceptions.user.UserSettingsNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final ObjectMapper objectMapper;

    public UserSettingsService(UserSettingsRepository userSettingsRepository,
                               ObjectMapper objectMapper) {
        this.userSettingsRepository = userSettingsRepository;
        this.objectMapper = objectMapper;
    }

    public void updateSettings(SingleFieldPatch patchUpdate) throws JsonProcessingException {
        UserSettingsEntity userSettings = userSettingsRepository.findByUserEntity_Id(patchUpdate.getUserId())
                .orElseThrow(() -> new UserSettingsNotFoundException("User settings for user ID " + patchUpdate.getUserId() + " does not exist"));
        ObjectReader reader = objectMapper.readerForUpdating(userSettings);
        String json = objectMapper.writeValueAsString(
                Map.of(patchUpdate.getField(), patchUpdate.getValue())
        );
        reader.readValue(json);
        // Test that individual properties are updated
        userSettingsRepository.save(userSettings);

        // Call WS method to confirm if settings applied are accepted, otherwise rollback in frontend
    }

}
