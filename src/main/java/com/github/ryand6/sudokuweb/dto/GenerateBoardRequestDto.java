package com.github.ryand6.sudokuweb.dto;

import lombok.*;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateBoardRequestDto {

    @NotNull(message = "Difficulty is required")
    private String difficulty;

    @NotNull(message = "Lobby ID is required")
    private Long lobbyId;

}
