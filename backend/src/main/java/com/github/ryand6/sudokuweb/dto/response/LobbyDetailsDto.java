package com.github.ryand6.sudokuweb.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.enums.GameType;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LobbyDetailsDto {

    private Long id;

    private String lobbyName;

    @JsonProperty("isActive")
    private boolean isActive;

    private boolean inGame;

    private Long currentGameId;

    private GameType gameType;

    private GameMode gameMode;

    private Difficulty difficulty;

    private TimeLimitPreset timeLimitPreset;

}
