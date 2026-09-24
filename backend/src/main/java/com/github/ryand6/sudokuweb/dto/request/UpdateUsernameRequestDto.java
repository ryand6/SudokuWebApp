package com.github.ryand6.sudokuweb.dto.request;

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
public class UpdateUsernameRequestDto {

    @NotBlank
    @Size(min=3, max=10)
    @Pattern(regexp="\\S+", message="Must not contain whitespace")
    @NoProfanity(message="Username contains inappropriate language")
    private String username;

}
