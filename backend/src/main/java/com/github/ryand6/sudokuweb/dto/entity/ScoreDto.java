package com.github.ryand6.sudokuweb.dto.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScoreDto {

    private Long id;

    private Integer totalScore;

    private Integer gamesPlayed;

}
