package com.github.ryand6.sudokuweb.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRecoveryEmailRequestDto {

    @NotBlank(message = "Recovery email is required")
    @Email(message = "Must be a valid email address")
    private String recoveryEmail;

}
