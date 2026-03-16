package com.github.ryand6.sudokuweb.dto.entity.lobby;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LobbySettingsDto {

    @JsonProperty("isPublic")
    private boolean isPublic;

    private Difficulty difficulty;

    private TimeLimitPreset timeLimit;

    private GameMode gameMode;

}
