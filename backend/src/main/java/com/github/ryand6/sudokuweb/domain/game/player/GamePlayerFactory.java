package com.github.ryand6.sudokuweb.domain.game.player;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.PlayerColour;

public class GamePlayerFactory {

    public static GamePlayerEntity createGamePlayer(GameEntity game, UserEntity user, PlayerColour playerColour, boolean isGameStateShared, String initialBoardState) {
        GamePlayerEntity gamePlayer = new GamePlayerEntity();
        gamePlayer.setId(new GamePlayerId(game.getId(), user.getId()));
        gamePlayer.setGameEntity(game);
        gamePlayer.setUserEntity(user);

        gamePlayer.setPlayerColour(playerColour);

        // Setup player state
        GamePlayerStateEntity gamePlayerState = new GamePlayerStateEntity();
        // Each player gets their own board state only if this isn't a shared state determined by the game mode
        if (!isGameStateShared) {
            gamePlayerState.setCurrentBoardState(initialBoardState);
        }
        gamePlayerState.setGamePlayerEntity(gamePlayer);

        // Attach children to parent
        gamePlayer.setGamePlayerStateEntity(gamePlayerState);

        return gamePlayer;
    }

}
