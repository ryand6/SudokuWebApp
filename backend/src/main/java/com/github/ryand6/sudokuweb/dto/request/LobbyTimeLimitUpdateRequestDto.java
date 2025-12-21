package com.github.ryand6.sudokuweb.dto.request;

import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LobbyTimeLimitUpdateRequestDto {

    private TimeLimitPreset timeLimit;

}
