package com.github.ryand6.sudokuweb.events.types.game;

import com.github.ryand6.sudokuweb.domain.game.player.state.NoteUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoteUpdateRejectedEvent {

    private Long gameId;

    private Long userId;

    private NoteUpdate noteUpdate;

}
