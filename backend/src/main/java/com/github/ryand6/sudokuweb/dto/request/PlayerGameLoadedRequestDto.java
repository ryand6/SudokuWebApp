package com.github.ryand6.sudokuweb.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerGameLoadedRequestDto {

    @NotNull(message = "User ID is required")
    private Long userId;

}
