package com.github.ryand6.sudokuweb.controllers.ws;

import com.github.ryand6.sudokuweb.domain.game.player.state.NoteUpdate;
import com.github.ryand6.sudokuweb.dto.request.ClearNotesRequestDto;
import com.github.ryand6.sudokuweb.dto.request.SubmitCellUpdateRequestDto;
import com.github.ryand6.sudokuweb.dto.request.SubmitNoteUpdateRequestDto;
import com.github.ryand6.sudokuweb.services.game.GamePlayerStateService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GamePlayerStateWsController {

    private final GamePlayerStateService gamePlayerStateService;

    public GamePlayerStateWsController(GamePlayerStateService gamePlayerStateService) {
        this.gamePlayerStateService = gamePlayerStateService;
    }

    @MessageMapping("/game/{gameId}/user/{userId}/submit-cell-update")
    public void submitCellUpdate(
            @DestinationVariable Long gameId,
            @DestinationVariable Long userId,
            @Valid SubmitCellUpdateRequestDto requestDto) {
        gamePlayerStateService.handleCellUpdateSubmission(gameId, userId, requestDto.getRow(), requestDto.getCol(), requestDto.getValue());
    }

    @MessageMapping("/game/{gameId}/user/{userId}/submit-note-update")
    public void submitNoteUpdate(
            @DestinationVariable Long gameId,
            @DestinationVariable Long userId,
            @Valid SubmitNoteUpdateRequestDto requestDto) {
        gamePlayerStateService.handleNoteUpdateSubmission(gameId, userId, new NoteUpdate(requestDto.getRow(), requestDto.getCol(), requestDto.getNote()));
    }

    @MessageMapping("/game/{gameId}/user/{userId}/clear-notes")
    public void clearNotes(
            @DestinationVariable Long gameId,
            @DestinationVariable Long userId,
            @Valid ClearNotesRequestDto requestDto) {
        gamePlayerStateService.clearCellNotes(gameId, userId, requestDto.getRow(), requestDto.getCol());
    }

}
