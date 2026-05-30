package com.github.ryand6.sudokuweb.events.types.lobby;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LobbyGameEndedEvent {

    private Long lobbyId;

}
