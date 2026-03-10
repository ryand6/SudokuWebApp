package com.github.ryand6.sudokuweb.events.types.lobby;

import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyDto;

public interface LobbyUpdateWsEvent {

    public LobbyDto getLobbyDto();

}
