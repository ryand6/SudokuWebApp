package com.github.ryand6.sudokuweb.domain.user.oauth;

import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_oauth_providers")
public class UserOAuthProviderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_oauth_providers_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    // Name of OAuth2 provider User is registered using
    @Column(name = "provider", nullable = false)
    private String provider;

    // Unique OAuth2 provider ID used to authenticate
    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserOAuthProviderEntity that)) return false;
        return Objects.equals(provider, that.getProvider()) &&
                Objects.equals(providerId, that.getProviderId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, providerId);
    }

}
