package com.github.ryand6.sudokuweb.events.types.lobby;

import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyChatMessageDto;

public interface LobbyChatMessageSentWsEvent {

    public LobbyChatMessageDto getLobbyChatMessageDto();

}
