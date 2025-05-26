package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Returns UserEntity if exists based on combination of OAuth2 provider and provider ID
    Optional<UserEntity> findByProviderAndProviderId(String provider, String providerId);

    // Checks to see if username already taken
    boolean existsByUsername(String username);

}
