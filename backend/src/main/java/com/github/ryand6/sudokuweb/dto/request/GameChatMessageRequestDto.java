package com.github.ryand6.sudokuweb.dto.request;

import com.github.ryand6.sudokuweb.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameChatMessageRequestDto {

    @NotBlank
    @Size(min=1, max=100, message="Messages must be between 1 and 100 characters in length.")
    private String message;

    private MessageType messageType;

}
