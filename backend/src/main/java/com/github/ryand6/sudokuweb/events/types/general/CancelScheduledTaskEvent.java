package com.github.ryand6.sudokuweb.events.types.general;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CancelScheduledTaskEvent {

    private String taskId;

}
