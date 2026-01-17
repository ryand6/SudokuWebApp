package com.github.ryand6.sudokuweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {

    // Injectable bean used to return current system's time in UTC.
    // The bean allows for different instances of Clock to be passed to Instant.now(), allowing for deterministic testing of time when required.
    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }

}
