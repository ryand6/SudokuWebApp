package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.LobbyState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbyStateRepository extends CrudRepository<LobbyState, Long> {
}
