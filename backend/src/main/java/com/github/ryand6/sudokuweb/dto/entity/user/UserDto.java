package com.github.ryand6.sudokuweb.dto.entity.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String username;

    @JsonProperty("isOnline")
    private boolean isOnline;

    private UserStatsDto userStats;

    private UserSettingsDto userSettings;

}
