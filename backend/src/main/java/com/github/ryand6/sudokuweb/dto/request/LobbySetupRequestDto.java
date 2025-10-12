package com.github.ryand6.sudokuweb.dto.request;

import com.github.ryand6.sudokuweb.validation.NoProfanity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LobbySetupRequestDto {

    @NotBlank
    @Size(min=3, max=20)
    // Use custom profanity filter
    @NoProfanity(message="Username contains inappropriate language")
    private String lobbyName;

    @NotNull
    private Boolean isPublic;

}
