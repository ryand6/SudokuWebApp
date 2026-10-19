package com.github.ryand6.sudokuweb.events.types.lobby.ws;

import com.github.ryand6.sudokuweb.domain.lobby.settings.LobbyDifficultyUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LobbyDifficultySettingsUpdatedWsEvent {

    private LobbyDifficultyUpdate lobbyDifficultyUpdate;

}
