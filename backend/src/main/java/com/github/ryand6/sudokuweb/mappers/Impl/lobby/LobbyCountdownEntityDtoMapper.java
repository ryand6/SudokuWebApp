package com.github.ryand6.sudokuweb.mappers.Impl.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.countdown.LobbyCountdownEntity;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyCountdownDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class LobbyCountdownEntityDtoMapper implements EntityDtoMapper<LobbyCountdownEntity, LobbyCountdownDto> {

    @Override
    public LobbyCountdownDto mapToDto(LobbyCountdownEntity lobbyCountdown) {
        return LobbyCountdownDto.builder()
                .countdownActive(lobbyCountdown.isCountdownActive())
                .countdownEndsAt(lobbyCountdown.getCountdownEndsAt())
                .countdownInitiatedBy(lobbyCountdown.getCountdownInitiatedBy())
                .build();
    }

}
