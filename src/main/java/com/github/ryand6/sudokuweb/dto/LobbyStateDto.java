package com.github.ryand6.sudokuweb.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LobbyStateDto {

    private Long id;

    private Long userId;

    private String username;

    private Long puzzleId;

    private Integer score;

    private String currentBoardState;

    private LocalDateTime lastActive;

}
