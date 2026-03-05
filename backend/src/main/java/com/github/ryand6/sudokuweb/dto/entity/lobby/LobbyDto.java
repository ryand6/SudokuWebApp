package com.github.ryand6.sudokuweb.dto.entity.lobby;

import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/*
LobbyDto used for transferring necessary Lobby entity fields to frontend
*/
public class LobbyDto {

    private Long id;

    private String lobbyName;

    private Instant createdAt;

    private boolean isActive;

    private boolean inGame;

    private Long currentGameId;

    private LobbySettingsDto lobbySettings;

    private LobbyCountdownDto lobbyCountdown;

    private Set<LobbyPlayerDto> lobbyPlayers;

    private UserDto host;

}
