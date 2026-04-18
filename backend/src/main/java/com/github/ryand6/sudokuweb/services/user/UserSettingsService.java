package com.github.ryand6.sudokuweb.services.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.ryand6.sudokuweb.domain.user.settings.UserSettingsEntity;
import com.github.ryand6.sudokuweb.domain.user.settings.UserSettingsRepository;
import com.github.ryand6.sudokuweb.domain.user.settings.SingleFieldPatch;
import com.github.ryand6.sudokuweb.events.types.user.ws.UserSettingsFieldRejectedWsEvent;
import com.github.ryand6.sudokuweb.events.types.user.ws.UserSettingsUpdatedWsEvent;
import com.github.ryand6.sudokuweb.events.types.user.ws.UserSettingsValueRejectedWsEvent;
import com.github.ryand6.sudokuweb.exceptions.user.UserSettingsNotFoundException;
import com.github.ryand6.sudokuweb.util.OAuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Map;

@Service
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public UserSettingsService(UserSettingsRepository userSettingsRepository,
                               ObjectMapper objectMapper,
                               ApplicationEventPublisher applicationEventPublisher) {
        this.userSettingsRepository = userSettingsRepository;
        this.objectMapper = objectMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private static final Logger log = LoggerFactory.getLogger(UserSettingsService.class);

    public void updateSettings(Long userId, OAuth2User principal, OAuth2AuthenticationToken authToken, SingleFieldPatch patchUpdate) {
        UserSettingsEntity userSettings = userSettingsRepository.findByUserEntity_Id(userId)
                .orElseThrow(() -> new UserSettingsNotFoundException("User settings for user ID " + userId + " does not exist"));

        String providerName = OAuthUtil.retrieveOAuthProviderName(authToken);
        String providerId = OAuthUtil.retrieveOAuthProviderId(providerName, principal);

        try {
            UserSettingsEntity.class.getDeclaredField(patchUpdate.getField());
        } catch (NoSuchFieldException e) {
            applicationEventPublisher.publishEvent(
                    new UserSettingsFieldRejectedWsEvent(providerId, patchUpdate)
            );
            return;
        }

        // Use reflection to get original value prior to update in case the new value is rejected
        Object originalValue;
        try {
            Field originalField = UserSettingsEntity.class.getDeclaredField(patchUpdate.getField());
            originalField.setAccessible(true);
            originalValue = originalField.get(userSettings);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Failed to retrieve original value for field: {}", patchUpdate.getField(), e);
            return;
        }

        try {
            ObjectReader reader = objectMapper.readerForUpdating(userSettings);
            String json = objectMapper.writeValueAsString(
                    Map.of(patchUpdate.getField(), patchUpdate.getValue())
            );
            reader.readValue(json);
        } catch (JsonProcessingException e) {
            applicationEventPublisher.publishEvent(
                    new UserSettingsValueRejectedWsEvent(
                            providerId,
                            new SingleFieldPatch(patchUpdate.getField(), originalValue))
            );
        }

        // Test that individual properties are updated
        userSettingsRepository.save(userSettings);

        applicationEventPublisher.publishEvent(
                new UserSettingsUpdatedWsEvent(providerId, patchUpdate)
        );
    }

}
