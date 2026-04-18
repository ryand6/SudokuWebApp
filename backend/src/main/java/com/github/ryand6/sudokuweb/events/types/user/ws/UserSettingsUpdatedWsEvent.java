package com.github.ryand6.sudokuweb.events.types.user.ws;

import com.github.ryand6.sudokuweb.domain.user.settings.SingleFieldPatch;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSettingsUpdatedWsEvent {

    private String providerId;

    private SingleFieldPatch singleFieldPatch;

}
