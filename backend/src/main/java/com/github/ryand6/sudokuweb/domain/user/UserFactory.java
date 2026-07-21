package com.github.ryand6.sudokuweb.domain.user;

import com.github.ryand6.sudokuweb.domain.user.oauth.UserOAuthProviderEntity;
import com.github.ryand6.sudokuweb.domain.user.settings.UserSettingsEntity;

import java.util.HashSet;
import java.util.Set;

public class UserFactory {

    public static UserEntity createUser(String username, String provider, String providerId, String recoveryEmailHash) {
        UserSettingsEntity userSettings = new UserSettingsEntity();
        // Persist the user entity to DB
        UserEntity newUser = new UserEntity();
        UserOAuthProviderEntity oAuthProvider = new UserOAuthProviderEntity();
        oAuthProvider.setProvider(provider);
        oAuthProvider.setProviderId(providerId);
        oAuthProvider.setUserEntity(newUser);

        userSettings.setUserEntity(newUser);

        newUser.setUsername(username);
        newUser.setOnline(true);
        newUser.setUserSettingsEntity(userSettings);
        newUser.setUserOAuthProviderEntities(new HashSet<>(Set.of(oAuthProvider)));
        newUser.setRecoveryEmailHash(recoveryEmailHash);
        return newUser;
    }

}
