package com.github.ryand6.sudokuweb.dto;

import com.github.ryand6.sudokuweb.validation.NoProfanity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    // No whitespace allowed
    @Pattern(regexp="\\S+", message="Must not contain whitespace")
    // Use custom profanity filter
    @NoProfanity(message="Username contains inappropriate language")
    private String username;

}
