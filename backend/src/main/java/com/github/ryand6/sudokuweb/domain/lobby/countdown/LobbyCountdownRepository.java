package com.github.ryand6.sudokuweb.domain.lobby.countdown;

import com.github.ryand6.sudokuweb.domain.lobby.countdown.LobbyCountdownEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbyCountdownRepository extends JpaRepository<LobbyCountdownEntity, Long> {

}
