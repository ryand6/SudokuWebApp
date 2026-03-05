package com.github.ryand6.sudokuweb.domain.game.player;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.player.settings.GamePlayerSettingsEntity;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.PlayerColour;

public class GamePlayerFactory {

    public static GamePlayerEntity createGamePlayer(GameEntity game, UserEntity user, PlayerColour playerColour, boolean isGameStateShared, String initialBoardState) {
        GamePlayerId gamePlayerId = new GamePlayerId(game.getId(), user.getId());
        GamePlayerEntity gamePlayer = new GamePlayerEntity();
        gamePlayer.setId(gamePlayerId);

        // Setup player state
        GamePlayerStateEntity gamePlayerState = new GamePlayerStateEntity();
        // Each player gets their own board state only if this isn't a shared state determined by the game mode
        if (!isGameStateShared) {
            gamePlayerState.setCurrentBoardState(initialBoardState);
        }
        gamePlayerState.setGamePlayerEntity(gamePlayer);

        // Set up player settings
        GamePlayerSettingsEntity gamePlayerSettings = new GamePlayerSettingsEntity();
        gamePlayerSettings.setGamePlayerEntity(gamePlayer);

        // Attach children to parent
        gamePlayer.setGamePlayerStateEntity(gamePlayerState);
        gamePlayer.setGamePlayerSettingsEntity(gamePlayerSettings);

        gamePlayer.setPlayerColour(playerColour);

        return gamePlayer;
    }

}
