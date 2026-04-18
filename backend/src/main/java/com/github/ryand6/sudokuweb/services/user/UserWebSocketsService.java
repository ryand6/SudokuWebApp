package com.github.ryand6.sudokuweb.services.user;

import com.github.ryand6.sudokuweb.domain.user.settings.SingleFieldPatch;
import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import com.github.ryand6.sudokuweb.events.types.user.ws.UserSettingsUpdatedWsEvent;
import com.github.ryand6.sudokuweb.events.types.user.ws.UserUpdateWsEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Service
public class UserWebSocketsService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public UserWebSocketsService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    // Emmit event notification when a user update occurs
    public void handleUserUpdate(UserDto userDto, String providerId) {
        // Create a message header detailing what type of update this is so that the frontend can respond accordingly
        Map<String, Object> messageHeader = Map.of(
                "type", "USER_UPDATED",
                "payload", userDto
        );
        // Send the updated user Dto over websockets to that user's topic - Spring automatically prefixes path with "user" and maps to the current user using the providerId
        simpMessagingTemplate.convertAndSendToUser(providerId, "/queue/updates", messageHeader);
    }

    public void handleUserSettingsUpdate(String providerId, SingleFieldPatch singleFieldPatch) {
        Map<String, Object> messageHeader = Map.of(
                "type", "USER_SETTINGS_UPDATED",
                "payload", singleFieldPatch
        );
        simpMessagingTemplate.convertAndSendToUser(providerId, "/queue/updates", messageHeader);
    }

    public void handleUserSettingsFieldRejected(String providerId, SingleFieldPatch singleFieldPatch) {
        Map<String, Object> messageHeader = Map.of(
                "type", "USER_SETTINGS_FIELD_REJECTED",
                "payload", singleFieldPatch
        );
        simpMessagingTemplate.convertAndSendToUser(providerId, "/queue/updates", messageHeader);
    }

    public void handleUserSettingsValueRejected(String providerId, SingleFieldPatch singleFieldPatch) {
        Map<String, Object> messageHeader = Map.of(
                "type", "USER_SETTINGS_VALUE_REJECTED",
                "payload", singleFieldPatch
        );
        simpMessagingTemplate.convertAndSendToUser(providerId, "/queue/updates", messageHeader);
    }

    // Used for listening to user update events
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleUserUpdateWsEvent(UserUpdateWsEvent event) {
        handleUserUpdate(event.getUserDto(), event.getProviderId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleUserSettingsUpdateWsEvent(UserSettingsUpdatedWsEvent event) {
        handleUserSettingsUpdate(event.getProviderId(), event.getSingleFieldPatch());
    }

    @EventListener()
    void handleUserSettingsFieldRejectedWsEvent(UserSettingsUpdatedWsEvent event) {
        handleUserSettingsFieldRejected(event.getProviderId(), event.getSingleFieldPatch());
    }

    @EventListener()
    void handleUserSettingsValueRejectedWsEvent(UserSettingsUpdatedWsEvent event) {
        handleUserSettingsValueRejected(event.getProviderId(), event.getSingleFieldPatch());
    }

}
