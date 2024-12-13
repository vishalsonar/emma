package com.sonar.vishal.emma.util;

import com.sonar.vishal.emma.context.Context;
import com.sonar.vishal.emma.entity.CompanyNameData;
import com.sonar.vishal.emma.entity.Data;
import com.sonar.vishal.emma.entity.FrequencyData;
import com.sonar.vishal.emma.entity.TaskData;
import com.sonar.vishal.emma.listener.CompanyNameEditorCloseListener;
import com.sonar.vishal.emma.listener.CompanyNameItemDoubleClickListener;
import com.sonar.vishal.emma.service.FireBaseService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.dom.Style;
import org.springframework.context.annotation.Profile;

import java.util.Date;

@Profile("ADMIN")
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
    private static final String ZERODHA_NAME = "Zerodha Name";
    private static final String COMPANY_NAME = "Company Name";
    private static final String TWENTY_FIVE_PERCENTAGE = "25%";
    private static final String ONE_ZERO_ZERO_PERCENTAGE = "100%";
    private static final String LAST_TRADE_PRICE = "Last Trade Price";
    private static final String TASK_LAST_EXECUTION = "Last Execution";
    private static final String PERCENTAGE_CHANGE = "Percentage Change";
    private static final String ECONOMICS_TIMES_NAME = "Economic Times Name";


    public static Span getLogo() {
        Span logo = Context.getBean(Span.class, EMMA);
        Style logoStyle = logo.getStyle();
        logoStyle.set(WIDTH, ONE_ZERO_ZERO_PERCENTAGE);
        logoStyle.set(FONT_SIZE, FOUR_ZERO_PX);
        logoStyle.set(TEXT_ALIGN, CENTER);
        return logo;
    }

    public static Span getDateTime() {
        String dateTime = Context.getBean(Date.class).toString();
        Span time = Context.getBean(Span.class, dateTime);
        Style timeStyle = time.getStyle();
        timeStyle.set(WIDTH, ONE_ZERO_ZERO_PERCENTAGE);
        timeStyle.set(TEXT_ALIGN, CENTER);
        return time;
    }

    public static Grid<Data> getGrid() {
        Grid<Data> grid = Context.getBean(Grid.class, Data.class, false);
        grid.addColumn(Data::getCompanyName).setHeader(COMPANY_NAME).setWidth(FIFTY_PERCENTAGE);
        grid.addColumn(Data::getLastTradePrice).setHeader(LAST_TRADE_PRICE).setWidth(TWENTY_FIVE_PERCENTAGE);
        grid.addColumn(Data::getPercentageChange).setHeader(PERCENTAGE_CHANGE).setWidth(TWENTY_FIVE_PERCENTAGE);
        grid.setWidthFull();
        grid.setHeightFull();
        return grid;
    }

    public static Grid<FrequencyData> getFrequencyGrid() {
        Grid<FrequencyData> grid = Context.getBean(Grid.class, FrequencyData.class, false);
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
        Grid<TaskData> grid = Context.getBean(Grid.class, TaskData.class, false);
        grid.addColumn(TaskData::getName).setHeader(TASK_NAME).setWidth(FIFTY_PERCENTAGE);
        grid.addColumn(TaskData::getLastExecution).setHeader(TASK_LAST_EXECUTION).setWidth(FIFTY_PERCENTAGE);
        grid.setWidthFull();
        grid.setHeightFull();
        return grid;
    }

    public static Grid<CompanyNameData> getCompanyNameGrid(FireBaseService fireBaseService) {
        Grid<CompanyNameData> grid = Context.getBean(Grid.class, CompanyNameData.class, false);
        Binder<CompanyNameData> companyNameDataBinder = Context.getBean(Binder.class, CompanyNameData.class);
        Editor<CompanyNameData> companyNameDataEditor = grid.getEditor();
        TextField zerodhaNameTextField = Context.getBean(TextField.class);
        zerodhaNameTextField.setWidthFull();
        companyNameDataEditor.setBinder(companyNameDataBinder);
        companyNameDataEditor.addCloseListener(editorCloseEvent -> Context.getBean(CompanyNameEditorCloseListener.class).onEditorClose(editorCloseEvent));
        companyNameDataBinder.forField(zerodhaNameTextField).bind(CompanyNameData::getZerodhaName, CompanyNameData::setZerodhaName);
        grid.addItemDoubleClickListener(doubleClickEvent -> Context.getBean(CompanyNameItemDoubleClickListener.class).setEditor(companyNameDataEditor).onComponentEvent(doubleClickEvent));
        grid.addColumn(CompanyNameData::getEconomicTimesName).setHeader(ECONOMICS_TIMES_NAME).setWidth(FIFTY_PERCENTAGE);
        grid.addColumn(CompanyNameData::getZerodhaName).setHeader(ZERODHA_NAME).setWidth(FIFTY_PERCENTAGE).setEditorComponent(zerodhaNameTextField);
        grid.setWidthFull();
        grid.setHeightFull();
        return grid;
    }

    public static Notification getNotification(String message, boolean isErrorNotification) {
        Notification notification = Context.getBean(Notification.class, message);
        notification.addThemeVariants(isErrorNotification ? NotificationVariant.LUMO_ERROR : NotificationVariant.LUMO_PRIMARY);
        notification.setPosition(Notification.Position.TOP_END);
        notification.setDuration(5000);
        return notification;
    }
}
