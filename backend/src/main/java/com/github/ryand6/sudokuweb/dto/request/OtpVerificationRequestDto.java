package com.github.ryand6.sudokuweb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpVerificationRequestDto {

    @NotBlank
    @Pattern(regexp = "\\d{6}", message = "OTP must be a 6 digit number")
    private String otp;

}
