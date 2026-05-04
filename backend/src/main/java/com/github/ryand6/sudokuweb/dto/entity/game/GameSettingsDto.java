package com.github.ryand6.sudokuweb.dto.entity.game;

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
public class GameSettingsDto {

    private Difficulty difficulty;

    private TimeLimitPreset timeLimit;

    private GameMode gameMode;

    private GameType gameType;

}
