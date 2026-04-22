package com.github.ryand6.sudokuweb.controllers.ws;

import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import com.github.ryand6.sudokuweb.domain.user.settings.SingleFieldPatch;
import com.github.ryand6.sudokuweb.exceptions.auth.OAuth2LoginRequiredException;
import com.github.ryand6.sudokuweb.services.user.UserService;
import com.github.ryand6.sudokuweb.services.user.UserSettingsService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;

@Controller
public class UserSettingsWsController {

    private final UserSettingsService userSettingsService;
    private final UserService userService;

    public UserSettingsWsController(UserSettingsService userSettingsService,
                                    UserService userService) {
        this.userSettingsService = userSettingsService;
        this.userService = userService;
    }

    @MessageMapping("/user/update-settings")
    public void updateUserSettings(@AuthenticationPrincipal OAuth2User principal,
                                   OAuth2AuthenticationToken authToken,
                                   SingleFieldPatch requestDto) {
        if (principal == null || authToken == null) {
            throw new OAuth2LoginRequiredException("OAuth2 login required to carry out this action");
        }
        UserDto user = userService.getCurrentUserByOAuth(principal, authToken);

        System.out.println("Field Update: " + requestDto.getField());
        System.out.println("Value Update: " + requestDto.getValue());

        userSettingsService.updateSettings(user.getId(), principal, authToken, requestDto);
    }

}
