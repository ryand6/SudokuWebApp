package com.github.ryand6.sudokuweb.dto.response;

import com.github.ryand6.sudokuweb.dto.entity.game.GameChatMessageDto;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyChatMessageDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetGameChatMessagesResponseDto {

    private List<GameChatMessageDto> gameChatMessages;

}
