package com.sonar.vishal.emma.util;

import com.google.common.eventbus.EventBus;
import com.sonar.vishal.emma.bus.LogEventListener;

public class Constant {

    public static final EventBus LOG_EVENT_BUS = new EventBus("LOG EVENT BUS");
    public static final EventBus ORDER_EVENT_BUS = new EventBus("ORDER EVENT BUS");

    static {
        LOG_EVENT_BUS.register(new LogEventListener());
    }

    private Constant() {
        // Prevent Instantiation
    }

    public static final String TIME_15_30 = "15:30";
    public static final String ASIA_KOLKATA = "Asia/Kolkata";

    public static final String PIPE = "|";
    public static final String PIPE_REGEX = "\\|";
    public static final String ROUND_DECIMAL_REGEX = "%.2f";

    public static final String EMPTY = "";
    public static final String HTML_HR = "<hr>";
    public static final String TASK_STATUS = "Task Status";
    public static final String GAINER_TODAY = "Gainer Today";
    public static final String GAINER_WEEK = "Gainer Week";
    public static final String GAINER_MONTH = "Gainer Month";
    public static final String GAINER_FREQUENCY = "Gainer Frequency";
    public static final String DOCUMENT_DATE_FORMAT_PATTERN = "dd-MM-yyyy";
    public static final String DATE_TIME_FORMAT_PATTERN = "dd-MM-yyyy HH:mm:ss";

    public static final String COMMA = ",";
    public static final String HYPHEN = "-";
    public static final String SPACE_REGEX = " ";
    public static final String NEW_LINE_REGEX = "\n";
    public static final String PERCENTAGE_REGEX = "%";
    public static final String CHROME_OPTION_HEADLESS = "--headless";
    public static final String DOCUMENT_PARSE_DATE_FORMAT_PATTERN = "dd-MMM,-yyyy";

    public static final String ANALYTICS = "ANALYTICS";
    public static final String FREQUENCY = "FREQUENCY";
    public static final String TASK = "TASK";
    public static final String MAP_COMPANY_NAME = "Map Company Name";
    public static final String COMPANY_NAME_MAP = "COMPANY_NAME_MAP";
    public static final String SERVICE_ACCOUNT_FILE_NAME = "emma-service-account.json";
    public static final String SYSTEM_SERVICE_ACCOUNT = "SYSTEM_SERVICE_ACCOUNT";

    public static final String FREQUENCY_MODULATION_TASK_NAME = "Frequency Modulation Task";
    public static final String DATA_AQUISITION_TASK_NAME = "Data Acquisition Task";
    public static final String KITE_CONNECT_TASK_NAME = "Kite Connect Task";
}
