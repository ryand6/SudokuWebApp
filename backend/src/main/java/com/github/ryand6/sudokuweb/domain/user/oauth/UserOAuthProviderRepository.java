package com.github.ryand6.sudokuweb.domain.user.oauth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOAuthProviderRepository extends JpaRepository<UserOAuthProviderEntity, Long> {
}
