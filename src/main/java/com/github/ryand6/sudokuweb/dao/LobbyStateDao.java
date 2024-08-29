package com.github.ryand6.sudokuweb.dao;

import com.github.ryand6.sudokuweb.domain.LobbyState;

import java.util.Optional;

public interface LobbyStateDao {

    void create(LobbyState lobbyState);

    Optional<LobbyState> findOne(Long stateId);

}
