package com.github.ryand6.sudokuweb.dao;

import com.github.ryand6.sudokuweb.domain.Lobby;

import java.util.List;
import java.util.Optional;

public interface LobbyDao {

    void create(Lobby lobby);

    Optional<Lobby> findOne(Long lobbyId);

    List<Lobby> find();

}
