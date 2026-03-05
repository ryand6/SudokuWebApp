package com.github.ryand6.sudokuweb.domain.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.countdown.LobbyCountdownEntity;
import com.github.ryand6.sudokuweb.domain.lobby.settings.LobbySettingsEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;

public class LobbyFactory {

    public static LobbyEntity createLobby(UserEntity requester, String lobbyName, boolean isPublic) {
        LobbyEntity newLobby = new LobbyEntity();
        newLobby.setActive(true);
        newLobby.setLobbyName(lobbyName);
        // requester of lobby creation becomes the host
        newLobby.setHost(requester);

        LobbySettingsEntity lobbySettings = new LobbySettingsEntity();
        lobbySettings.setPublic(isPublic);
        newLobby.setLobbySettingsEntity(lobbySettings);
        lobbySettings.setLobbyEntity(newLobby);

        LobbyCountdownEntity lobbyCountdown = new LobbyCountdownEntity();
        newLobby.setLobbyCountdownEntity(lobbyCountdown);
        lobbyCountdown.setLobbyEntity(newLobby);

        return newLobby;
    }

}
