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
public class LobbyState {

    private Long id;

    private Long lobby_id;

    private Long user_id;

    private Long puzzle_id;

    private String current_board_state;

    private Integer score;

    private LocalDateTime last_active;

}
