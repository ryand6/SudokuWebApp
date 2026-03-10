package com.github.ryand6.sudokuweb.events.types.lobby.ws;

import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyDto;
import lombok.*;

@Getter
@AllArgsConstructor
public class LobbyUpdatePostGameCreationWsEvent implements LobbyUpdateWsEvent {

    private LobbyDto lobbyDto;

}
