package com.sonar.vishal.emma.util;

import java.util.Calendar;

public class TaskUtil {

    public static boolean inBusinessHour() {
        boolean state = false;
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && currentHour >= 9 && currentHour < 16) {
            state = true;
        }
        return state;
    }
}
