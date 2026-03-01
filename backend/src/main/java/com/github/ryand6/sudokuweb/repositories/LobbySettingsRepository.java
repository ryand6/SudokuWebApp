package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.lobby.LobbySettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbySettingsRepository extends JpaRepository<LobbySettingsEntity, Long> {

}
