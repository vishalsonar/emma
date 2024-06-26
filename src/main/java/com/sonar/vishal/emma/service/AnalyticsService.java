package com.sonar.vishal.emma.service;

import com.sonar.vishal.emma.entity.Data;
import com.sonar.vishal.emma.entity.FrequencyData;
import com.sonar.vishal.emma.entity.TaskData;
import com.sonar.vishal.emma.util.ComponentUtil;
import com.sonar.vishal.emma.util.Constant;
import com.vaadin.flow.component.grid.Grid;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class AnalyticsService implements Serializable {

    private SimpleDateFormat dateFormat;

    private FireBaseService fireBaseService;

    public AnalyticsService() {
        fireBaseService = new FireBaseService();
        dateFormat = new SimpleDateFormat(Constant.DOCUMENT_DATE_FORMAT_PATTERN);
    }

    public Grid<FrequencyData> getFrequencyDataGrid() {
        Grid<FrequencyData> frequencyDataGrid = ComponentUtil.getFrequencyGrid();
        List<FrequencyData> frequencyDataList = new ArrayList<>(fireBaseService.getFrequencyData().entrySet().stream()
                .map(entry -> createFrequencyData(entry.getKey(), String.valueOf(entry.getValue()))).toList());
        if (frequencyDataList.isEmpty()) {
            ComponentUtil.getNotification("Failed to load Gainer Frequency.", true).open();
        }
        frequencyDataGrid.setItems(sortFrequencyDataByOccurrence(frequencyDataList));
        return frequencyDataGrid;
    }

    public Grid<TaskData> getTaskStatusDataGrid() {
        Grid<TaskData> taskStatusDataGrid = ComponentUtil.getTaskStatusGrid();
        List<TaskData> taskStatusDataList = new ArrayList<>(fireBaseService.getTaskStatus().entrySet().stream().map(entry -> {
            TaskData taskData = new TaskData();
            taskData.setName(entry.getKey());
            taskData.setLastExecution(entry.getValue().toString());
            return taskData;
        }).collect(Collectors.toList()));
        if (taskStatusDataList.isEmpty()) {
            ComponentUtil.getNotification("Failed to load Task Status.", true).open();
        }
        taskStatusDataGrid.setItems(taskStatusDataList);
        return taskStatusDataGrid;
    }

    public Grid<Data> getTodayDataGrid() {
        List<Data> dataList = new ArrayList<>();
        Grid<Data> grid = ComponentUtil.getGrid();
        Map<String, Data> dataMap = fireBaseService.getCollectionMapData(dateFormat.format(new Date()));
        if (dataMap != null && !dataMap.isEmpty()) {
            dataList = dataMap.entrySet().stream().map(Map.Entry::getValue).toList();
        } else {
            ComponentUtil.getNotification("Failed to load Gainer Today Data.", true).open();
        }
        grid.setItems(sortDataByPercentageChange(new ArrayList<>(dataList)));
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
        return frequencyData;
    }

    private Grid<Data> getGridData(Calendar startOfWeekCalender, Calendar endOfWeekCalender, String errorMessage) {
        List<Data> dataList = new ArrayList<>();
        Map<String, Data> dataMap = new HashMap<>();
        Map<String, Data> tempDataMap = null;
        Grid<Data> grid = ComponentUtil.getGrid();
        while (startOfWeekCalender.before(endOfWeekCalender)) {
            tempDataMap = fireBaseService.getCollectionMapData(dateFormat.format(startOfWeekCalender.getTime()));
            if (tempDataMap != null && !tempDataMap.isEmpty()) {
                tempDataMap.entrySet().forEach(entry -> dataMap.put(entry.getKey(), entry.getValue()));
            }
            startOfWeekCalender.add(Calendar.DATE, 1);
        }
        if (!dataMap.isEmpty()) {
            dataList = dataMap.entrySet().stream().map(Map.Entry::getValue).toList();
        }
        if (dataList.isEmpty()) {
            ComponentUtil.getNotification(errorMessage, true).open();
        }
        grid.setItems(sortDataByPercentageChange(new ArrayList<>(dataList)));
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

    private List<Data> sortDataByPercentageChange(List<Data> dataList) {
        AtomicReference<Float> data1PercentageChange = new AtomicReference<>(0F);
        AtomicReference<Float> data2PercentageChange = new AtomicReference<>(0F);
        Collections.sort(dataList, (data1, data2) -> {
            data1PercentageChange.set(Float.valueOf(data1.getPercentageChange()));
            data2PercentageChange.set(Float.valueOf(data2.getPercentageChange()));
            if (data1PercentageChange.get() > data2PercentageChange.get()) {
                return -1;
            }
            if (data1PercentageChange.get() < data2PercentageChange.get()) {
                return 1;
            }
            return 0;
        });
        return dataList;
    }

    private List<FrequencyData> sortFrequencyDataByOccurrence(List<FrequencyData> frequencyDataList) {
        AtomicLong frequencyData1Occurrence = new AtomicLong(0);
        AtomicLong frequencyData2Occurrence = new AtomicLong(0);
        Collections.sort(frequencyDataList, (frequencyData1, frequencyData2) -> {
            frequencyData1Occurrence.set(Long.valueOf(frequencyData1.getOccurrence()));
            frequencyData2Occurrence.set(Long.valueOf(frequencyData2.getOccurrence()));
            if (frequencyData1Occurrence.get() > frequencyData2Occurrence.get()) {
                return -1;
            }
            if (frequencyData1Occurrence.get() < frequencyData2Occurrence.get()) {
                return 1;
            }
            return 0;
        });
        return frequencyDataList;
    }
}
