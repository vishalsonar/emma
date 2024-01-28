package com.sonar.vishal.emma.view;

import com.sonar.vishal.emma.service.EmmaAnalyticsService;
import com.sonar.vishal.emma.util.EmmaComponentUtil;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.Route;

@Route("")
public class EmmaDashBoardView extends VerticalLayout {

    private EmmaAnalyticsService analyticsService;

    public EmmaDashBoardView() {
        setWidthFull();
        setHeightFull();
        analyticsService = new EmmaAnalyticsService();
        add(EmmaComponentUtil.getLogo());
        add(new Html("<hr>"));

        TabSheet tabSheet = new TabSheet();
        tabSheet.add("Gainer Today", analyticsService.getTodayDataGrid());
        tabSheet.add("Gainer Week", analyticsService.getWeekDataGrid());
        tabSheet.add("Gainer Month", analyticsService.getMonthDataGrid());
        tabSheet.add("Gainer Frequency", analyticsService.getFrequencyDataGrid());
        tabSheet.setWidthFull();
        tabSheet.setHeightFull();
        add(tabSheet);
    }
}
