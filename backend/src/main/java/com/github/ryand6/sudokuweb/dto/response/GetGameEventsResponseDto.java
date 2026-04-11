package com.github.ryand6.sudokuweb.dto.response;

import com.github.ryand6.sudokuweb.dto.entity.game.GameEventDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetGameEventsResponseDto {

    private List<GameEventDto> gameEvents;

}
