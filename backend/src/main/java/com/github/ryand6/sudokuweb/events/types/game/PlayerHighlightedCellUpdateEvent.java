package com.github.ryand6.sudokuweb.events.types.game;

import com.github.ryand6.sudokuweb.dto.events.PlayerHighlightedCellDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerHighlightedCellUpdateEvent {

    private PlayerHighlightedCellDto playerHighlightedCellDto;

}
