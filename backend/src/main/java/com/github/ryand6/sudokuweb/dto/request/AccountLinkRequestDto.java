package com.github.ryand6.sudokuweb.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountLinkRequestDto {

    @NotBlank
    @Email(message = "Must be a valid email address")
    private String email;

}
