package com.sonar.vishal.emma.util;

import com.sonar.vishal.emma.entity.Data;
import com.sonar.vishal.emma.entity.FrequencyData;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.dom.Style;

public class ComponentUtil {

    private static final String EMMA = "EMMA";
    private static final String FIFTY_PERCENTAGE = "50%";
    private static final String OCCURRENCE = "Occurrence";
    private static final String COMPANY_NAME = "Company Name";
    private static final String TWENTY_FIVE_PERCENTAGE = "25%";
    private static final String LAST_TRADE_PRICE = "Last Trade Price";
    private static final String PERCENTAGE_CHANGE = "Percentage Change";

    public static Span getLogo() {
        Span logo = new Span(EMMA);
        Style logoStyle = logo.getStyle();
        logoStyle.set("width", "100%");
        logoStyle.set("font-size", "40px");
        logoStyle.set("text-align", "center");
        return logo;
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
        grid.addColumn(FrequencyData::getCompanyName).setHeader(COMPANY_NAME);
        grid.addColumn(FrequencyData::getOccurrence).setHeader(OCCURRENCE);
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
