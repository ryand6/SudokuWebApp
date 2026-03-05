package com.github.ryand6.sudokuweb.dto.entity.game;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GamePlayerSettingsDto {

    private boolean showOtherPlayerHighlightedSquares;

}
