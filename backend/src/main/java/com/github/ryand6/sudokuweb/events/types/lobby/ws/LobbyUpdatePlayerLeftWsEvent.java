package com.github.ryand6.sudokuweb.events.types.lobby.ws;

import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LobbyUpdatePlayerLeftWsEvent implements LobbyUpdateWsEvent {

    private LobbyDto lobbyDto;

}
