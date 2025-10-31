package com.github.ryand6.sudokuweb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LobbyChatMessageRequestDto {

    private Long lobbyId;

    private Long userId;

    private String username;

    @NotBlank
    @Size(min=3, max=250)
    private String message;

}
