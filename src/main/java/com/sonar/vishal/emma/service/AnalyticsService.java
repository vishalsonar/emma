package com.sonar.vishal.emma.service;

import com.sonar.vishal.emma.entity.CompanyNameData;
import com.sonar.vishal.emma.entity.Data;
import com.sonar.vishal.emma.entity.FrequencyData;
import com.sonar.vishal.emma.entity.TaskData;
import com.sonar.vishal.emma.util.ComponentUtil;
import com.sonar.vishal.emma.util.Constant;
import com.vaadin.flow.component.grid.Grid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AnalyticsService implements Serializable {

    private SimpleDateFormat dateFormat;

    @Autowired
    private FireBaseService fireBaseService;

    public AnalyticsService() {
        dateFormat = new SimpleDateFormat(Constant.DOCUMENT_DATE_FORMAT_PATTERN);
    }

    public Grid<FrequencyData> getFrequencyDataGrid() {
        List<FrequencyData> frequencyDataList = new ArrayList<>();
        Grid<FrequencyData> frequencyDataGrid = ComponentUtil.getFrequencyGrid();
        fireBaseService.getFrequencyData().forEach((key, value) -> frequencyDataList.add(createFrequencyData(key, String.valueOf(value))));
        if (frequencyDataList.isEmpty()) {
            ComponentUtil.getNotification("Failed to load Gainer Frequency.", true).open();
        }
        Collections.sort(frequencyDataList, (data1, data2) -> compareLong(data1.getOccurrence(), data2.getOccurrence()));
        frequencyDataGrid.setItems(frequencyDataList);
        return frequencyDataGrid;
    }

    public Grid<TaskData> getTaskStatusDataGrid() {
        List<TaskData> taskStatusDataList = new ArrayList<>();
        Grid<TaskData> taskStatusDataGrid = ComponentUtil.getTaskStatusGrid();
        fireBaseService.getTaskStatus().forEach((key, value) -> taskStatusDataList.add(createTaskData(key, String.valueOf(value))));
        if (taskStatusDataList.isEmpty()) {
            ComponentUtil.getNotification("Failed to load Task Status.", true).open();
        }
        taskStatusDataGrid.setItems(taskStatusDataList);
        return taskStatusDataGrid;
    }

    public Grid<CompanyNameData> getCompanyNameData() {
        List<CompanyNameData> companyNameDataList = new ArrayList<>();
        Grid<CompanyNameData> companyNameDataGrid = ComponentUtil.getCompanyNameGrid(fireBaseService);
        fireBaseService.getCompanyNameData().forEach((key, value) -> {
            if (String.valueOf(value).equals(Constant.EMPTY)) {
                companyNameDataList.add(createCompanyNameData(key, Constant.EMPTY));
            }
        });
        if (companyNameDataList.isEmpty()) {
            ComponentUtil.getNotification("Failed to load Company Name Data Map.", true).open();
        }
        companyNameDataGrid.setItems(companyNameDataList);
        return companyNameDataGrid;
    }

    public Grid<Data> getTodayDataGrid() {
        Grid<Data> grid = ComponentUtil.getGrid();
        List<Data> dataList = new ArrayList<>(fireBaseService.getCollectionMapData(dateFormat.format(new Date())).values());
        if (dataList.isEmpty()) {
            ComponentUtil.getNotification("Failed to load Gainer Today Data.", true).open();
        }
        Collections.sort(dataList, (data1, data2) -> compareDouble(data1.getPercentageChange(), data2.getPercentageChange()));
        grid.setItems(dataList);
        return grid;
    }

    public Grid<Data> getWeekDataGrid() {
        return getGridData(getCalendar(1), getCalendar(7), "Failed to load Gainer Week Data.");
    }

    public Grid<Data> getMonthDataGrid() {
        return getGridData(getStartOfMonthCalendar(), getEndOfMonthCalendar(), "Failed to load Gainer Month Data.");
    }

    private FrequencyData createFrequencyData(String companyName, String occurrence) {
        String[] valueString = occurrence.split(Constant.PIPE_REGEX);
        FrequencyData frequencyData = new FrequencyData();
        frequencyData.setCompanyName(companyName);
        frequencyData.setOccurrence(valueString[0]);
        frequencyData.setAveragePercentage(valueString[1]);
        frequencyData.setxDot(String.format(Constant.ROUND_DECIMAL_REGEX, Double.valueOf(frequencyData.getAveragePercentage()) / Double.valueOf(frequencyData.getOccurrence())));
        frequencyData.setxDotDot(String.format(Constant.ROUND_DECIMAL_REGEX, Double.valueOf(frequencyData.getxDot()) / Double.valueOf(frequencyData.getOccurrence())));
        return frequencyData;
    }

    private TaskData createTaskData(String name, String lastExecution) {
        TaskData taskData = new TaskData();
        taskData.setName(name);
        taskData.setLastExecution(lastExecution);
        return taskData;
    }

    private CompanyNameData createCompanyNameData(String economicTimesName, String zerodhaName) {
        CompanyNameData companyNameData = new CompanyNameData();
        companyNameData.setEconomicTimesName(economicTimesName);
        companyNameData.setZerodhaName(zerodhaName);
        return companyNameData;
    }

    private Grid<Data> getGridData(Calendar startOfWeekCalender, Calendar endOfWeekCalender, String errorMessage) {
        Map<String, Data> dataMap = new HashMap<>();
        Grid<Data> grid = ComponentUtil.getGrid();
        while (startOfWeekCalender.before(endOfWeekCalender)) {
            Optional.of(fireBaseService.getCollectionMapData(dateFormat.format(startOfWeekCalender.getTime()))).ifPresent(tempDataMap -> tempDataMap.forEach(dataMap::put));
            startOfWeekCalender.add(Calendar.DATE, 1);
        }
        List<Data> dataList = new ArrayList<>(dataMap.values());
        if (dataList.isEmpty()) {
            ComponentUtil.getNotification(errorMessage, true).open();
        }
        Collections.sort(dataList, (data1, data2) -> compareDouble(data1.getPercentageChange(), data2.getPercentageChange()));
        grid.setItems(dataList);
        return grid;
    }

    private Calendar getCalendar(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, offset - calendar.get(Calendar.DAY_OF_WEEK));
        return calendar;
    }

    private Calendar getStartOfMonthCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar;
    }

    private Calendar getEndOfMonthCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar;
    }

    private int compareDouble(String data1, String data2) {
        return -Double.compare(Double.valueOf(data1), Double.valueOf(data2));
    }

    private int compareLong(String data1, String data2) {
        return -Long.compare(Long.valueOf(data1), Long.valueOf(data2));
    }
}
