package com.sonar.vishal.emma.util;

import com.google.common.eventbus.EventBus;
import com.sonar.vishal.emma.bus.LogEventListener;

public class Constant {

    public static final EventBus eventBus = new EventBus();

    static {
        eventBus.register(new LogEventListener());
    }

    private Constant() {
        // Prevent Instantiation
    }

    public static final String TIME_15_30 = "15:30";
    public static final String ASIA_KOLKATA = "Asia/Kolkata";

    public static final String PIPE = "|";
    public static final String PIPE_REGEX = "\\|";

    public static final String EMPTY = "";
    public static final String HTML_HR = "<hr>";
    public static final String GAINER_TODAY = "Gainer Today";
    public static final String GAINER_WEEK = "Gainer Week";
    public static final String GAINER_MONTH = "Gainer Month";
    public static final String GAINER_FREQUENCY = "Gainer Frequency";
    public static final String DOCUMENT_DATE_FORMAT_PATTERN = "dd-MM-yyyy";

    public static final String COMMA = ",";
    public static final String HYPHEN = "-";
    public static final String SPACE_REGEX = " ";
    public static final String NEW_LINE_REGEX = "\n";
    public static final String PERCENTAGE_REGEX = "%";
    public static final String CHROME_OPTION_HEADLESS = "--headless";
    public static final String DOCUMENT_PARSE_DATE_FORMAT_PATTERN = "dd-MMM,-yyyy";

    public static final String ANALYTICS = "ANALYTICS";
    public static final String FREQUENCY = "FREQUENCY";
    public static final String SERVICE_ACCOUNT_FILE_NAME = "emma-service-account.json";
}
