package com.github.ryand6.sudokuweb.events.types.lobby.ws;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyGameCreationUpdate;
import lombok.*;

@Getter
@AllArgsConstructor
public class LobbyUpdatePostGameCreationWsEvent {

    private LobbyGameCreationUpdate lobbyGameCreationUpdate;

}
