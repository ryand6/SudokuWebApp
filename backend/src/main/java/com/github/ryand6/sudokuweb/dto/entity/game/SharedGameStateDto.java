package com.github.ryand6.sudokuweb.dto.entity.game;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SharedGameStateDto {

    private Map<Integer, Long> cellFirstOwnership;

    private String currentSharedBoardState;

}
