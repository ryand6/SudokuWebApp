package com.github.ryand6.sudokuweb.dto.request;

import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateBoardRequestDto {

    @NotNull(message = "Lobby Dto is required")
    private LobbyDto lobby;

}
