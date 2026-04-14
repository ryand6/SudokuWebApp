package com.github.ryand6.sudokuweb.domain.user.settings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettingsEntity, Long> {

    Optional<UserSettingsEntity> findByUserEntity_Id(Long userId);

}
