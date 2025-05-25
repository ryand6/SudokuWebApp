package com.github.ryand6.sudokuweb.domain.factory;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;

import java.util.HashSet;
import java.util.Set;

public class LobbyFactory {

    // Create a public lobby (no joinCode required)
    public static LobbyEntity createLobby(String lobbyName, UserEntity host) {
        Set<UserEntity> activeUsers = new HashSet<>();
        activeUsers.add(host);

        LobbyEntity lobbyEntity = new LobbyEntity();
        lobbyEntity.setLobbyName(lobbyName);
        lobbyEntity.setIsActive(true);
        lobbyEntity.setIsPublic(true);
        lobbyEntity.setInGame(false);
        lobbyEntity.setUserEntities(activeUsers);
        lobbyEntity.setHost(host);
        return lobbyEntity;
    }

    // Create a private lobby (joinCode required)
    public static LobbyEntity createLobby(String lobbyName, String joinCode, UserEntity host) {
        Set<UserEntity> activeUsers = new HashSet<>();
        activeUsers.add(host);

        LobbyEntity lobbyEntity = new LobbyEntity();
        lobbyEntity.setLobbyName(lobbyName);
        lobbyEntity.setIsActive(true);
        lobbyEntity.setIsPublic(false);
        lobbyEntity.setInGame(false);
        lobbyEntity.setJoinCode(joinCode);
        lobbyEntity.setUserEntities(activeUsers);
        lobbyEntity.setHost(host);
        return lobbyEntity;
    }

}
