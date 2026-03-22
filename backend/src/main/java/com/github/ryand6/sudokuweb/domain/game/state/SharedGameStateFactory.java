package com.github.ryand6.sudokuweb.domain.game.state;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;

public class SharedGameStateFactory {

    public static SharedGameStateEntity createSharedGameStateWithoutSharedBoardState(GameEntity game) {
        SharedGameStateEntity sharedGameState = new SharedGameStateEntity();
        sharedGameState.setGameEntity(game);
        return sharedGameState;
    }

    public static SharedGameStateEntity createSharedGameStateWithSharedBoardState(GameEntity game, String sharedBoardState) {
        SharedGameStateEntity sharedGameState = new SharedGameStateEntity();
        sharedGameState.setGameEntity(game);
        sharedGameState.setCurrentSharedBoardState(sharedBoardState);
        return sharedGameState;
    }

}
