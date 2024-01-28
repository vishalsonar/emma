package com.sonar.vishal.emma.util;

import com.sonar.vishal.emma.entity.EmmaData;
import com.sonar.vishal.emma.entity.EmmaFrequencyData;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.dom.Style;

public class EmmaComponentUtil {

    public static final String EMMA = "EMMA";

    public static Span getLogo() {
        Span logo = new Span(EMMA);
        Style logoStyle = logo.getStyle();
        logoStyle.set("width", "100%");
        logoStyle.set("font-size", "40px");
        logoStyle.set("text-align", "center");
        return logo;
    }

    public static Grid<EmmaData> getGrid() {
        Grid<EmmaData> grid = new Grid<>(EmmaData.class, false);
        grid.addColumn(EmmaData::getCompanyName).setHeader("Company Name").setWidth("20%");
        grid.addColumn(EmmaData::getLastTradePrice).setHeader("Last Trade Price");
        grid.addColumn(EmmaData::getChange).setHeader("Change Price");
        grid.addColumn(EmmaData::getPercentageChange).setHeader("Percentage Change").setWidth("10%");
        grid.addColumn(EmmaData::getDayHigh).setHeader("Day High Price");
        grid.addColumn(EmmaData::getDayLow).setHeader("Day Low Price");
        grid.addColumn(EmmaData::getVolume).setHeader("Volume");
        grid.setWidthFull();
        grid.setHeightFull();
        return grid;
    }

    public static Grid<EmmaFrequencyData> getFrequencyGrid() {
        Grid<EmmaFrequencyData> grid = new Grid<>(EmmaFrequencyData.class, false);
        grid.addColumn(EmmaFrequencyData::getCompanyName).setHeader("Company Name");
        grid.addColumn(EmmaFrequencyData::getOccurrence).setHeader("Occurrence");
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
