package com.github.ryand6.sudokuweb.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PlayerRemovedEvent {

    private Long lobbyId;

    private Long userId;

}
