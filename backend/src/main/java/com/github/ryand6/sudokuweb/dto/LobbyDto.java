package com.github.ryand6.sudokuweb.dto;

import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
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

    private Difficulty difficulty;

    private TimeLimitPreset timeLimit;

    private Boolean isActive;

    private Boolean isPublic;

    private Boolean countdownActive;

    private Instant countdownEndsAt;

    private Long countdownInitiatedBy;

    private Boolean settingsLocked;

    private Set<LobbyPlayerDto> lobbyPlayers;

    private Long hostId;

}
