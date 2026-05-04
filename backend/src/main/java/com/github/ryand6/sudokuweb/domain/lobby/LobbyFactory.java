package com.github.ryand6.sudokuweb.domain.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.countdown.LobbyCountdownEntity;
import com.github.ryand6.sudokuweb.domain.lobby.settings.LobbySettingsEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.enums.GameType;

public class LobbyFactory {

    public static LobbyEntity createLobby(UserEntity requester, String lobbyName, boolean isPublic, GameMode gameMode, GameType gameType) {
        LobbyEntity newLobby = new LobbyEntity();
        newLobby.setActive(true);
        newLobby.setLobbyName(lobbyName);
        // requester of lobby creation becomes the host
        newLobby.setHost(requester);

        LobbySettingsEntity lobbySettings = new LobbySettingsEntity();
        lobbySettings.setPublic(isPublic);
        lobbySettings.setGameMode(gameMode);
        lobbySettings.setGameType(gameType);
        newLobby.setLobbySettingsEntity(lobbySettings);
        lobbySettings.setLobbyEntity(newLobby);

        LobbyCountdownEntity lobbyCountdown = new LobbyCountdownEntity();
        newLobby.setLobbyCountdownEntity(lobbyCountdown);
        lobbyCountdown.setLobbyEntity(newLobby);

        return newLobby;
    }

}
