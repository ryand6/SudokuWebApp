package com.github.ryand6.sudokuweb.controllers.rest.game;

import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import com.github.ryand6.sudokuweb.dto.request.RevertInGameStatusRequestDto;
import com.github.ryand6.sudokuweb.dto.response.RevertInGameStatusResponseDto;
import com.github.ryand6.sudokuweb.services.lobby.LobbyPlayerService;
import com.github.ryand6.sudokuweb.services.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game-player-state")
public class GamePlayerStateRestController {

    private final LobbyPlayerService lobbyPlayerService;
    private final UserService userService;

    public GamePlayerStateRestController(LobbyPlayerService lobbyPlayerService,
                                         UserService userService) {
        this.lobbyPlayerService = lobbyPlayerService;
        this.userService = userService;
    }

    @PostMapping("/revert-in-game-status")
    public ResponseEntity<?> revertInGameStatus(@AuthenticationPrincipal OAuth2User principal,
                                                OAuth2AuthenticationToken authToken,
                                                @Valid @RequestBody RevertInGameStatusRequestDto requestDto) {
        UserDto currentUser = userService.getCurrentUserByOAuth(principal, authToken);
        RevertInGameStatusResponseDto responseDto = lobbyPlayerService.revertLobbyPlayerInGameStatus(requestDto.getLobbyId(), currentUser.getId());
        return ResponseEntity.ok(responseDto);
    }

}
