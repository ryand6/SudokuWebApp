package com.github.ryand6.sudokuweb.domain.lobby;

import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountdownEvaluationResult {

    private boolean shouldUpdate;

    private boolean shouldCancel;

    @Getter
    private Instant countdownEndsAt;

    @Getter
    private Long newInitiator;

    public boolean shouldCountdownUpdate() {
        return shouldUpdate;
    }

    public boolean shouldCountdownCancel() {
        return shouldCancel;
    }

}
