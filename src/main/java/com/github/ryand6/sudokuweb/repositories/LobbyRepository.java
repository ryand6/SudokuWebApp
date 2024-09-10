package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.Lobby;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbyRepository extends CrudRepository<Lobby, Long> {
}
