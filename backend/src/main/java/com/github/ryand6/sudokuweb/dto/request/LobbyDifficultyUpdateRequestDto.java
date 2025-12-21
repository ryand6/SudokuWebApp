package com.github.ryand6.sudokuweb.dto.request;

import com.github.ryand6.sudokuweb.enums.Difficulty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LobbyDifficultyUpdateRequestDto {

    private Difficulty difficulty;

}
