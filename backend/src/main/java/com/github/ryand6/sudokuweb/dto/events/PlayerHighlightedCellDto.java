package com.github.ryand6.sudokuweb.dto.events;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerHighlightedCellDto {

    private Long gameId;

    private Long userId;

    private int row;

    private int col;

}
