package com.github.ryand6.sudokuweb.dto.response;

import com.github.ryand6.sudokuweb.dto.entity.LobbyChatMessageDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LobbyGetChatMessagesResponseDto {

    private List<LobbyChatMessageDto> lobbyChatMessages;

}
