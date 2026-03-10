package com.github.ryand6.sudokuweb.events.types.lobby;

import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyChatMessageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LobbyChatInfoMessageSentWsEvent implements LobbyChatMessageSentWsEvent {

    private LobbyChatMessageDto lobbyChatMessageDto;

}
