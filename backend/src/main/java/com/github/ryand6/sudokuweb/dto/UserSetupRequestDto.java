package com.github.ryand6.sudokuweb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSetupRequestDto {

    @NotBlank
    @Size(min=3, max=20)
    private String username;

}
