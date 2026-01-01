package com.github.ryand6.sudokuweb.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveLobbyRequestDto {

    @NotNull(message = "Lobby ID is required")
    private Long lobbyId;

}
