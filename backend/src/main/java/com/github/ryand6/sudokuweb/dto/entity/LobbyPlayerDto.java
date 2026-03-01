package com.github.ryand6.sudokuweb.dto.entity;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyPlayerId;
import com.github.ryand6.sudokuweb.enums.LobbyStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/*
LobbyPlayerDto used for transferring necessary Lobby entity fields to frontend
*/
public class LobbyPlayerDto {

    private LobbyPlayerId id;

    private UserDto user;

    private Instant joinedAt;

    private LobbyStatus lobbyStatus;

    private Instant readyAt;

    private Instant lobbyMessageTimestamp;

}
