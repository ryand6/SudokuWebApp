package com.github.ryand6.sudokuweb.dto.request;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRecoveryEmailRequestDto {

    @Email(message = "Must be a valid email address")
    private String recoveryEmail;

}
