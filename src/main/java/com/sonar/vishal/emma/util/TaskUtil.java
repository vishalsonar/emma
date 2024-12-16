package com.sonar.vishal.emma.util;

import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

@Profile({"TASK", "ADMIN"})
public class TaskUtil {

    private TaskUtil() {
        // Prevent Instantiation
    }

    public static String getIndiaDateTimeNow() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constant.DATE_TIME_FORMAT_PATTERN);
        dateTimeFormatter = dateTimeFormatter.withZone(ZoneId.of(Constant.ASIA_KOLKATA));
        return dateTimeFormatter.format(Instant.now());
    }

    public static synchronized boolean inBusinessHour() {
        boolean state = false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(Constant.ASIA_KOLKATA));
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && currentHour >= 9 && currentHour < 16) {
            state = true;
        }
        return state;
    }
}
