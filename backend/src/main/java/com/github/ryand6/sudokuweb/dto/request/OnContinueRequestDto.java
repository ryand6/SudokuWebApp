package com.github.ryand6.sudokuweb.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnContinueRequestDto {

    @NotNull(message = "Game ID is required")
    private Long gameId;

}
