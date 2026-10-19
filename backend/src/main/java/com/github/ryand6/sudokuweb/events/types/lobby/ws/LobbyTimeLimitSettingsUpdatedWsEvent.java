package com.github.ryand6.sudokuweb.events.types.lobby.ws;

import com.github.ryand6.sudokuweb.domain.lobby.settings.LobbyTimeLimitUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LobbyTimeLimitSettingsUpdatedWsEvent {

    private LobbyTimeLimitUpdate lobbyTimeLimitUpdate;

}
