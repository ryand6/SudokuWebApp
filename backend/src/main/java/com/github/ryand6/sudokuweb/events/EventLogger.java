package com.github.ryand6.sudokuweb.events;

import com.github.ryand6.sudokuweb.services.TaskSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EventLogger {

    private static final Logger log = LoggerFactory.getLogger(EventLogger.class);

    @EventListener
    public void logEvent(Object event) {
        log.debug("Event received: {}", event.getClass().getSimpleName());
    }

}
