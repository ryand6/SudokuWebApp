package com.github.ryand6.sudokuweb.dto.entity.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ryand6.sudokuweb.enums.Theme;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSettingsDto {

    private Theme theme;

    private boolean opponentHighlightedSquaresEnabled;

    private boolean highlightedHousesEnabled;

    private boolean highlightedFirstsEnabled;

    private boolean audioEnabled;

    private boolean gameChatNotificationsEnabled;

    private boolean scoreNotificationsEnabled;

    private boolean streakNotificationsEnabled;

}
