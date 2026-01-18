package com.github.ryand6.sudokuweb.schedulers;

import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.testcontainers.shaded.org.awaitility.Awaitility;

public class SchedulingFrequencyIntegrationTests extends AbstractIntegrationTest {

    @SpyBean
    private StartGameScheduler startGameScheduler;

    @Test
    void startGameScheduler_testJobRuns() {
        Awaitility.await()
                .atMost(Durations.TWO_SECONDS)
                .untilAsserted(() -> Mockito.verify(startGameScheduler, Mockito.times(1)).scheduleGameCreation());
    }

}
