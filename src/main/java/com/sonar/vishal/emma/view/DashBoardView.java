package com.sonar.vishal.emma.view;

import com.sonar.vishal.emma.service.AnalyticsService;
import com.sonar.vishal.emma.task.DateTimeTask;
import com.sonar.vishal.emma.util.ComponentUtil;
import com.sonar.vishal.emma.util.Constant;
import com.sonar.vishal.emma.util.TaskUtil;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Profile;

@Profile("ADMIN")
@Route(Constant.EMPTY)
public class DashBoardView extends VerticalLayout {

    private AnalyticsService analyticsService;

    public DashBoardView() {
        setWidthFull();
        setHeightFull();
        analyticsService = new AnalyticsService();
        add(ComponentUtil.getLogo());
        add(new Html(Constant.HTML_HR));
        add(ComponentUtil.getDateTime());

        TabSheet tabSheet = new TabSheet();
        tabSheet.add(Constant.GAINER_TODAY, analyticsService.getTodayDataGrid());
        tabSheet.add(Constant.GAINER_WEEK, analyticsService.getWeekDataGrid());
        tabSheet.add(Constant.GAINER_MONTH, analyticsService.getMonthDataGrid());
        tabSheet.add(Constant.GAINER_FREQUENCY, analyticsService.getFrequencyDataGrid());
        tabSheet.add(Constant.TASK_STATUS, analyticsService.getTaskStatusDataGrid());
        tabSheet.setWidthFull();
        tabSheet.setHeightFull();
        add(tabSheet);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        DateTimeTask.setCurrentUI(attachEvent.getUI());
        DateTimeTask.setUiConsumer(ui -> ui.access(() -> ui.getCurrentView().getElement().getChild(2).setText(TaskUtil.getIndiaDateTimeNow())));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        DateTimeTask.setCurrentUI(null);
    }
}
