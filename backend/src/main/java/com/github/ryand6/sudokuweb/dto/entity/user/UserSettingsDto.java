package com.github.ryand6.sudokuweb.dto.entity.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSettingsDto {

    @JsonProperty("isDarkModeActive")
    private boolean isDarkModeActive;

    @JsonProperty("isSoundActive")
    private boolean isSoundActive;

}
