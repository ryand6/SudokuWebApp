package com.github.ryand6.sudokuweb.events.types.lobby.ws;

import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerJoinedUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LobbyUpdatePlayerJoinedWsEvent {

    private LobbyPlayerJoinedUpdate lobbyPlayerJoinedUpdate;

}
