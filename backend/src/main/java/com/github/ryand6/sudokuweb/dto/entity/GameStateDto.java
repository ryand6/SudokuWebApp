package com.github.ryand6.sudokuweb.dto.entity;

import com.github.ryand6.sudokuweb.enums.PlayerColour;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameStateDto {

    private Long id;

    private Long gameId;

    private UserDto user;

    private Integer score;

    private PlayerColour playerColour;

    private String currentBoardState;

    private byte[] notes;

}
