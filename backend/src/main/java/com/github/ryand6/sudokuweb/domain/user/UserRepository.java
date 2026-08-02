package com.github.ryand6.sudokuweb.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Returns UserEntity if exists based on combination of OAuth2 provider and provider ID
//    Optional<UserEntity> findByProviderAndProviderId(String provider, String providerId);

    // Checks to see if username already taken
    boolean existsByUsername(String username);

    @Query(
            value = """
                    SELECT user
                    FROM UserEntity user
                    JOIN user.userOAuthProviderEntities providers
                    WHERE providers.provider = :provider
                    AND providers.providerId = :providerId
                    """
    )
    Optional<UserEntity> findByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);

    Optional<UserEntity> findByRecoveryEmailHash(String recoveryEmailHash);

}
