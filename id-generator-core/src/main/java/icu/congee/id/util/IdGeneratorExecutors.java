package icu.congee.id.util;


import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class IdGeneratorExecutors {

    @Getter
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Getter
    private static final ScheduledExecutorService scheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor();

}
