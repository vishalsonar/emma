package com.sonar.vishal.emma.util;

import com.sonar.vishal.emma.entity.Data;
import com.sonar.vishal.emma.entity.FrequencyData;
import com.sonar.vishal.emma.entity.TaskData;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.dom.Style;

import java.util.Date;

public class ComponentUtil {

    private ComponentUtil() {
        // Prevent Instantiation
    }

    private static final String EMMA = "EMMA";
    private static final String DOT = "DOT";
    private static final String WIDTH = "width";
    private static final String CENTER = "center";
    private static final String DOT_DOT = "DOT DOT";
    private static final String FOUR_ZERO_PX = "40px";
    private static final String FONT_SIZE = "font-size";
    private static final String TASK_NAME = "Task Name";
    private static final String FIFTY_PERCENTAGE = "50%";
    private static final String TWENTY_PERCENTAGE = "20%";
    private static final String OCCURRENCE = "Occurrence";
    private static final String TEXT_ALIGN = "text-align";
    private static final String COMPANY_NAME = "Company Name";
    private static final String TWENTY_FIVE_PERCENTAGE = "25%";
    private static final String ONE_ZERO_ZERO_PERCENTAGE = "100%";
    private static final String LAST_TRADE_PRICE = "Last Trade Price";
    private static final String TASK_LAST_EXECUTION = "Last Execution";
    private static final String PERCENTAGE_CHANGE = "Percentage Change";

    public static Span getLogo() {
        Span logo = new Span(EMMA);
        Style logoStyle = logo.getStyle();
        logoStyle.set(WIDTH, ONE_ZERO_ZERO_PERCENTAGE);
        logoStyle.set(FONT_SIZE, FOUR_ZERO_PX);
        logoStyle.set(TEXT_ALIGN, CENTER);
        return logo;
    }

    public static Span getDateTime() {
        Span time = new Span(new Date().toString());
        Style timeStyle = time.getStyle();
        timeStyle.set(WIDTH, ONE_ZERO_ZERO_PERCENTAGE);
        timeStyle.set(TEXT_ALIGN, CENTER);
        return time;
    }

    public static Grid<Data> getGrid() {
        Grid<Data> grid = new Grid<>(Data.class, false);
        grid.addColumn(Data::getCompanyName).setHeader(COMPANY_NAME).setWidth(FIFTY_PERCENTAGE);
        grid.addColumn(Data::getLastTradePrice).setHeader(LAST_TRADE_PRICE).setWidth(TWENTY_FIVE_PERCENTAGE);
        grid.addColumn(Data::getPercentageChange).setHeader(PERCENTAGE_CHANGE).setWidth(TWENTY_FIVE_PERCENTAGE);
        grid.setWidthFull();
        grid.setHeightFull();
        return grid;
    }

    public static Grid<FrequencyData> getFrequencyGrid() {
        Grid<FrequencyData> grid = new Grid<>(FrequencyData.class, false);
        grid.addColumn(FrequencyData::getCompanyName).setHeader(COMPANY_NAME).setWidth(TWENTY_PERCENTAGE);
        grid.addColumn(FrequencyData::getOccurrence).setHeader(OCCURRENCE).setWidth(TWENTY_PERCENTAGE);
        grid.addColumn(FrequencyData::getAveragePercentage).setHeader(PERCENTAGE_CHANGE).setWidth(TWENTY_PERCENTAGE);
        grid.addColumn(FrequencyData::getxDot).setHeader(DOT).setWidth(TWENTY_PERCENTAGE);
        grid.addColumn(FrequencyData::getxDotDot).setHeader(DOT_DOT).setWidth(TWENTY_PERCENTAGE);
        grid.setWidthFull();
        grid.setHeightFull();
        return grid;
    }

    public static Grid<TaskData> getTaskStatusGrid() {
        Grid<TaskData> grid = new Grid<>(TaskData.class, false);
        grid.addColumn(TaskData::getName).setHeader(TASK_NAME).setWidth(FIFTY_PERCENTAGE);
        grid.addColumn(TaskData::getLastExecution).setHeader(TASK_LAST_EXECUTION).setWidth(FIFTY_PERCENTAGE);
        grid.setWidthFull();
        grid.setHeightFull();
        return grid;
    }

    public static Notification getNotification(String message, boolean isErrorNotification) {
        Notification notification = new Notification(message);
        notification.addThemeVariants(isErrorNotification ? NotificationVariant.LUMO_ERROR : NotificationVariant.LUMO_PRIMARY);
        notification.setPosition(Notification.Position.TOP_END);
        notification.setDuration(5000);
        return notification;
    }
}
