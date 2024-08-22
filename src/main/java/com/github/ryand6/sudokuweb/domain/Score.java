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

    private Long user_id;

    private Integer total_score;

    private Integer games_played;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

}
