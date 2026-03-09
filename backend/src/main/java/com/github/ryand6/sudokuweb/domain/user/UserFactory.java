package com.github.ryand6.sudokuweb.domain.user;

import com.github.ryand6.sudokuweb.domain.user.settings.UserSettingsEntity;
import com.github.ryand6.sudokuweb.domain.user.stats.UserStatsEntity;

public class UserFactory {

    public static UserEntity createUser(String username, String provider, String providerId) {
        // Create user stats and user settings entities first, so they can be persisted when User entity is persisted
        UserStatsEntity userStats = new UserStatsEntity();
        UserSettingsEntity userSettings = new UserSettingsEntity();
        // Persist the user entity to DB
        UserEntity newUser = new UserEntity();

        // assign parent to children
        userStats.setUserEntity(newUser);
        userSettings.setUserEntity(newUser);

        newUser.setUsername(username);
        newUser.setProvider(provider);
        newUser.setProviderId(providerId);
        newUser.setOnline(true);
        newUser.setUserStatsEntity(userStats);
        newUser.setUserSettingsEntity(userSettings);
        return newUser;
    }

}
