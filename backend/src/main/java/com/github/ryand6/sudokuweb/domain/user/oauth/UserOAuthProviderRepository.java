package com.github.ryand6.sudokuweb.domain.user.oauth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOAuthProviderRepository extends JpaRepository<UserOAuthProviderEntity, Long> {

    List<UserOAuthProviderEntity> findAllByUserEntity_Id(Long userId);

}
