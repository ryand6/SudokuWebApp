package com.github.ryand6.sudokuweb.domain.factory;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.LobbyPlayerId;
import com.github.ryand6.sudokuweb.domain.UserEntity;

public class LobbyPlayerFactory {

    public static LobbyPlayerEntity createLobbyPlayer(LobbyEntity lobby, UserEntity user) {
        // Create LobbyPlayerId composite key first
        LobbyPlayerId lobbyPlayerId = new LobbyPlayerId(lobby.getId(), user.getId());
        // create LobbyPlayerEntity
        LobbyPlayerEntity lobbyPlayerEntity = new LobbyPlayerEntity();
        lobbyPlayerEntity.setId(lobbyPlayerId);
        lobbyPlayerEntity.setLobby(lobby);
        lobbyPlayerEntity.setUser(user);
        // don't set joinedAt, done during first time record is persisted
        return lobbyPlayerEntity;
    }

}
