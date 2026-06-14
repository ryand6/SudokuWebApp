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

    @NotBlank
    @Size(min=1, max=100, message="Messages must be between 1 and 100 characters in length.")
    private String message;

}
