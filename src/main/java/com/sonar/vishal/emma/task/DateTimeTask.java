package com.sonar.vishal.emma.task;

import com.vaadin.flow.component.UI;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Profile("TASK")
public class DateTimeTask {

    private static UI ui;
    private static Consumer<UI> uiConsumer;

    public static void setCurrentUI(UI ui) {
        DateTimeTask.ui = ui;
    }

    public static void setUiConsumer(Consumer<UI> uiConsumer) {
        DateTimeTask.uiConsumer = uiConsumer;
    }

    @Scheduled(fixedRateString = "${application.date.time.scheduler.fixedRate.millisecond}")
    public void execute() {
        if (DateTimeTask.ui != null) {
            DateTimeTask.uiConsumer.accept(DateTimeTask.ui);
        }
    }
}
