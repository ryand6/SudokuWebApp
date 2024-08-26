package com.github.ryand6.sudokuweb.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Score {

    private Long id;

    private Long userId;

    private Integer totalScore;

    private Integer gamesPlayed;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
