package com.sonar.vishal.emma.task;

import com.sonar.vishal.emma.context.Context;
import com.vaadin.flow.component.UI;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
@Profile({"TASK", "ADMIN"})
public class DateTimeTask {

    public static final Map<UI, Consumer<UI>> uiConsumerMap = Context.getBean(ConcurrentHashMap.class);

    @Scheduled(fixedRateString = "${application.date.time.scheduler.fixedRate.millisecond}")
    public void execute() {
        uiConsumerMap.forEach((ui, consumer) -> {
            consumer.accept(ui);
        });
    }
}
