package com.github.ryand6.sudokuweb.controllers.rest.leaderboards;

import com.github.ryand6.sudokuweb.domain.leaderboards.TopFiveLeaderboardRow;
import com.github.ryand6.sudokuweb.dto.entity.leaderboards.LeaderboardsDto;
import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.services.leaderboards.LeaderboardsService;
import com.github.ryand6.sudokuweb.services.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboards")
public class LeaderboardsController {

    private final LeaderboardsService leaderboardsService;
    private final UserService userService;

    public LeaderboardsController(LeaderboardsService leaderboardsService,
                                  UserService userService) {
        this.leaderboardsService = leaderboardsService;
        this.userService = userService;
    }

    @GetMapping("/get-top-five-with-user-rank")
    public ResponseEntity<?> getTopFiveWithUserRank(@AuthenticationPrincipal OAuth2User principal,
                                                    OAuth2AuthenticationToken authToken,
                                                    @RequestParam GameMode gameMode) {
        UserDto user = userService.getCurrentUserByOAuth(principal, authToken);
        List<TopFiveLeaderboardRow> leaderboardRows = leaderboardsService.getTopFiveWithUserRank(user.getId(), gameMode);
        return ResponseEntity.ok(leaderboardRows);
    }

    @GetMapping("/get-stats")
    public ResponseEntity<?> getUserGameModeStats(@AuthenticationPrincipal OAuth2User principal,
                                              OAuth2AuthenticationToken authToken,
                                              @RequestParam GameMode gameMode) {
        UserDto user = userService.getCurrentUserByOAuth(principal, authToken);
        LeaderboardsDto leaderboardsDto = leaderboardsService.getUserGameModeStats(gameMode, user.getId());
        return ResponseEntity.ok(leaderboardsDto);
    }

}
