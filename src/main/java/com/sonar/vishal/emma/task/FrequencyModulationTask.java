package com.sonar.vishal.emma.task;

import com.sonar.vishal.emma.service.FireBaseService;
import com.sonar.vishal.emma.util.Constant;
import com.sonar.vishal.emma.util.TaskUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Profile("TASK")
public class FrequencyModulationTask {

    @Autowired
    private FireBaseService fireBaseService;

    @Scheduled(fixedRateString = "${application.frequency.scheduler.fixedRate.millisecond}")
    public void execute() {
        if (TaskUtil.inBusinessHour() && LocalTime.now(ZoneId.of(Constant.ASIA_KOLKATA)).isAfter(LocalTime.parse(Constant.TIME_15_30))) {
            String documentName = new SimpleDateFormat(Constant.DOCUMENT_DATE_FORMAT_PATTERN).format(new Date());
            fireBaseService.mergeFrequency(documentName);
            fireBaseService.updateTaskStatus(Constant.FREQUENCY_MODULATION_TASK_NAME);
        }
    }
}
