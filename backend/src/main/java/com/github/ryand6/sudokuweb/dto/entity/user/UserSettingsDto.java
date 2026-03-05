package com.github.ryand6.sudokuweb.dto.entity.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSettingsDto {

    private boolean isDarkModeActive;

    private boolean isSoundActive;

}
