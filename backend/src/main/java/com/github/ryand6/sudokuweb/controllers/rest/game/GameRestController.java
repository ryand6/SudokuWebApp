package com.github.ryand6.sudokuweb.controllers.rest.game;

import com.github.ryand6.sudokuweb.dto.entity.game.GameDto;
import com.github.ryand6.sudokuweb.dto.entity.game.PrivateGamePlayerStateDto;
import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import com.github.ryand6.sudokuweb.exceptions.auth.OAuth2LoginRequiredException;
import com.github.ryand6.sudokuweb.exceptions.game.player.GamePlayerNotFoundException;
import com.github.ryand6.sudokuweb.services.game.GameService;
import com.github.ryand6.sudokuweb.services.MembershipService;
import com.github.ryand6.sudokuweb.services.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/game")
public class GameRestController {

    private final GameService gameService;
    private final MembershipService membershipService;
    private final UserService userService;

    public GameRestController(GameService gameService,
                              MembershipService membershipService,
                              UserService userService) {
        this.gameService = gameService;
        this.membershipService = membershipService;
        this.userService = userService;
    }

    @GetMapping("/check-user-in-game")
    public ResponseEntity<?> isUserInGame(@RequestParam Long gameId,
                                          @AuthenticationPrincipal OAuth2User principal,
                                          OAuth2AuthenticationToken authToken) {
        // OAuth2 login not occurred yet
        if (principal == null || authToken == null) {
            throw new OAuth2LoginRequiredException("OAuth2 login required to carry out this action");
        }
        // cached
        UserDto user = userService.getCurrentUserByOAuth(principal, authToken);
        if (!membershipService.isUserInGame(gameId, user.getId())) {
            throw new GamePlayerNotFoundException("User with ID + " + user.getId() + " is not part of the game with ID " + gameId);
        } else {
            GameDto gameDto = gameService.getGameById(gameId);
            return ResponseEntity.ok(gameDto);
        }
    }

    // Get Game DTO using gameId
    @GetMapping("/get-game")
    public ResponseEntity<?> getGame(@RequestParam Long gameId) {
        GameDto gameDto = gameService.getGameById(gameId);
        return ResponseEntity.ok(gameDto);
    }

    @GetMapping("/get-game-player-state")
    public ResponseEntity<?> getGamePlayerState(@RequestParam Long gameId,
                                                @AuthenticationPrincipal OAuth2User principal,
                                                OAuth2AuthenticationToken authToken) {
        // OAuth2 login not occurred yet
        if (principal == null || authToken == null) {
            throw new OAuth2LoginRequiredException("OAuth2 login required to carry out this action");
        }
        // cached
        UserDto user = userService.getCurrentUserByOAuth(principal, authToken);
        PrivateGamePlayerStateDto privateGamePlayerState = gameService.getGamePlayerState(gameId, user.getId());
        return ResponseEntity.ok(privateGamePlayerState);
    }

}
