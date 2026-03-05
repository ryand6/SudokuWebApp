package com.github.ryand6.sudokuweb.dto.entity.game;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GamePlayerStateDto {

    private String currentBoardState;

    private byte[] notes;

    private int currentStreak;

    private double activeMultiplier;

    private Instant multiplierEndsAt;

}
