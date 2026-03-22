package com.github.ryand6.sudokuweb.exceptions.game.state;

public class GamePlayerStateOptimisticLockException extends RuntimeException {
    public GamePlayerStateOptimisticLockException(String message) {
        super(message);
    }
}
