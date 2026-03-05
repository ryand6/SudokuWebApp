package com.github.ryand6.sudokuweb.domain.game.state;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;

public class SharedGameStateFactory {

    public static SharedGameStateEntity createSharedGameState(GameEntity game, String sharedBoardState) {
        SharedGameStateEntity sharedGameState = new SharedGameStateEntity();
        sharedGameState.setGameEntity(game);
        sharedGameState.setCurrentSharedBoardState(sharedBoardState);
        return sharedGameState;
    }

}
