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

    private Long lobbyId;

    private Long userId;

    private Long puzzleId;

    private String currentBoardState;

    private Integer score;

    private LocalDateTime lastActive;

}
