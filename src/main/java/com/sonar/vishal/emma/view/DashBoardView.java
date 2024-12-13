package com.sonar.vishal.emma.view;

import com.sonar.vishal.emma.context.Context;
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

    public DashBoardView() {
        setWidthFull();
        setHeightFull();
        AnalyticsService analyticsService = Context.getBean(AnalyticsService.class);

        add(ComponentUtil.getLogo());
        add(Context.getBean(Html.class));
        add(ComponentUtil.getDateTime());

        TabSheet tabSheet = Context.getBean(TabSheet.class);
        tabSheet.add(Constant.GAINER_TODAY, analyticsService.getTodayDataGrid());
        tabSheet.add(Constant.GAINER_WEEK, analyticsService.getWeekDataGrid());
        tabSheet.add(Constant.GAINER_MONTH, analyticsService.getMonthDataGrid());
        tabSheet.add(Constant.GAINER_FREQUENCY, analyticsService.getFrequencyDataGrid());
        tabSheet.add(Constant.TASK_STATUS, analyticsService.getTaskStatusDataGrid());
        tabSheet.add(Constant.MAP_COMPANY_NAME, analyticsService.getCompanyNameData());
        tabSheet.setWidthFull();
        tabSheet.setHeightFull();
        add(tabSheet);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        DateTimeTask.uiConsumerMap.put(attachEvent.getUI(), ui -> ui.access(() -> ui.getCurrentView().getElement().getChild(2).setText(TaskUtil.getIndiaDateTimeNow())));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        DateTimeTask.uiConsumerMap.remove(detachEvent.getUI());
    }
}
