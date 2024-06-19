package com.sonar.vishal.emma.util;

import java.util.Calendar;
import java.util.TimeZone;

public class TaskUtil {

    private TaskUtil() {
        // Prevent Instantiation
    }

    public static boolean inBusinessHour() {
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
