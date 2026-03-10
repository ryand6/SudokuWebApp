package com.github.ryand6.sudokuweb.events.types.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.countdown.LobbyCountdownEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LobbyCountdownResetEvent {

    private LobbyCountdownEntity lobbyCountdown;

}
