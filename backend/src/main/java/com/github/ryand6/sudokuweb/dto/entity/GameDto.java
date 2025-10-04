package com.github.ryand6.sudokuweb.dto.entity;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameDto {

    private Long id;

    private LobbyDto lobby;

    private SudokuPuzzleDto sudokuPuzzle;

    private Set<GameStateDto> gameStates;

}
