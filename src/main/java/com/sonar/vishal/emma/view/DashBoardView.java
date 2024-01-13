package com.sonar.vishal.emma.view;

import com.sonar.vishal.emma.service.AnalyticsService;
import com.sonar.vishal.emma.util.ComponentUtil;
import com.sonar.vishal.emma.util.Constant;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.Route;

@Route(Constant.EMPTY)
public class DashBoardView extends VerticalLayout {

    private AnalyticsService analyticsService;

    public DashBoardView() {
        setWidthFull();
        setHeightFull();
        analyticsService = new AnalyticsService();
        add(ComponentUtil.getLogo());
        add(new Html(Constant.HTML_HR));

        TabSheet tabSheet = new TabSheet();
        tabSheet.add(Constant.GAINER_TODAY, analyticsService.getTodayDataGrid());
        tabSheet.add(Constant.GAINER_WEEK, analyticsService.getWeekDataGrid());
        tabSheet.add(Constant.GAINER_MONTH, analyticsService.getMonthDataGrid());
        tabSheet.add(Constant.GAINER_FREQUENCY, analyticsService.getFrequencyDataGrid());
        tabSheet.setWidthFull();
        tabSheet.setHeightFull();
        add(tabSheet);
    }
}
