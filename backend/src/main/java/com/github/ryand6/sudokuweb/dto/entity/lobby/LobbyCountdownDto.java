package com.github.ryand6.sudokuweb.dto.entity.lobby;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LobbyCountdownDto {

    private boolean countdownActive;

    private Instant countdownEndsAt;

    private Long countdownInitiatedBy;

}
