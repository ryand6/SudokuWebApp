package com.github.ryand6.sudokuweb.domain.lobby.player;

import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyCountdownDto;
import com.github.ryand6.sudokuweb.enums.LobbyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class LobbyPlayerStatusUpdate {

    private Long lobbyId;

    private Long userId;

    private LobbyStatus lobbyStatus;

    private Instant readyAt;

    private LobbyCountdownDto lobbyCountdown;

}
