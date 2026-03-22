package com.github.ryand6.sudokuweb.dto.entity.game;

import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import com.github.ryand6.sudokuweb.enums.GameResult;
import com.github.ryand6.sudokuweb.enums.PlayerColour;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GamePlayerDto {

    private UserDto user;

    private PlayerColour playerColour;

    private boolean[] boardProgress;

    private int score;

    private int firsts;

    private int mistakes;

    private int maxStreak;

    private boolean gameLoaded;

    private GameResult gameResult;

}
