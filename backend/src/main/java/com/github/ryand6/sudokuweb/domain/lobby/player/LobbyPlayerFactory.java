package com.github.ryand6.sudokuweb.domain.lobby.player;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;

public class LobbyPlayerFactory {

    public static LobbyPlayerEntity createLobbyPlayer(LobbyEntity lobby, UserEntity user) {
        // create LobbyPlayerEntity
        LobbyPlayerEntity lobbyPlayerEntity = new LobbyPlayerEntity();
        lobbyPlayerEntity.setId(new LobbyPlayerId(lobby.getId(), user.getId()));
        lobbyPlayerEntity.setLobby(lobby);
        lobbyPlayerEntity.setUser(user);
        // don't set joinedAt, done during first time record is persisted
        return lobbyPlayerEntity;
    }

}
