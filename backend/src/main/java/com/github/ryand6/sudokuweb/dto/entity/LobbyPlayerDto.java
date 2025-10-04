package com.github.ryand6.sudokuweb.dto.entity;

import com.github.ryand6.sudokuweb.domain.LobbyPlayerId;
import com.github.ryand6.sudokuweb.enums.LobbyStatus;
import com.github.ryand6.sudokuweb.enums.PreferenceDirection;
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

    private PreferenceDirection difficultyPreference;

    private Instant difficultyVoteTimestamp;

    private PreferenceDirection durationPreference;

    private Instant durationVoteTimestamp;

}
