package com.sonar.vishal.emma.service;

import com.sonar.vishal.emma.entity.EmmaData;
import com.sonar.vishal.emma.entity.EmmaFrequencyData;
import com.sonar.vishal.emma.util.EmmaComponentUtil;
import com.vaadin.flow.component.grid.Grid;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class EmmaAnalyticsService implements Serializable {

    private static final String DOCUMENT_DATE_FORMAT_PATTERN = "dd-MM-yyyy";

    private SimpleDateFormat dateFormat;

    private EmmaFireBaseService fireBaseService;

    public EmmaAnalyticsService() {
        fireBaseService = new EmmaFireBaseService();
        dateFormat = new SimpleDateFormat(DOCUMENT_DATE_FORMAT_PATTERN);
    }

    public Grid<EmmaFrequencyData> getFrequencyDataGrid() {
        Grid<EmmaFrequencyData> frequencyDataGrid = EmmaComponentUtil.getFrequencyGrid();
        List<EmmaFrequencyData> frequencyDataList = new ArrayList<>(fireBaseService.getFrequencyData().entrySet().stream()
                .map(entry -> createFrequencyData(entry.getKey(), String.valueOf(entry.getValue()))).toList());
        if (frequencyDataList.isEmpty()) {
            EmmaComponentUtil.getNotification("Failed to Load Gainer Frequency.", true).open();
        }
        frequencyDataGrid.setItems(sortEmmaFrequencyDataByOccurrence(frequencyDataList));
        return frequencyDataGrid;
    }

    public Grid<EmmaData> getTodayDataGrid() {
        List<EmmaData> emmaDataList = new ArrayList<>();
        Grid<EmmaData> grid = EmmaComponentUtil.getGrid();
        Map<String, EmmaData> emmaDataMap = fireBaseService.getCollectionMapData(dateFormat.format(new Date()));
        if (emmaDataMap != null && !emmaDataMap.isEmpty()) {
            emmaDataList = emmaDataMap.entrySet().stream().map(Map.Entry::getValue).toList();
        } else {
            EmmaComponentUtil.getNotification("Failed to Load Gainer Today Data.", true).open();
        }
        grid.setItems(sortEmmaDataByPercentageChange(new ArrayList<>(emmaDataList)));
        return grid;
    }

    public Grid<EmmaData> getWeekDataGrid() {
        return getGridData(getCalendar(1), getCalendar(7), "Failed to Load Gainer Week Data.");
    }

    public Grid<EmmaData> getMonthDataGrid() {
        return getGridData(getStartOfMonthCalendar(), getEndOfMonthCalendar(), "Failed to Load Gainer Month Data.");
    }

    private EmmaFrequencyData createFrequencyData(String companyName, String occurrence) {
        EmmaFrequencyData frequencyData = new EmmaFrequencyData();
        frequencyData.setCompanyName(companyName);
        frequencyData.setOccurrence(occurrence);
        return frequencyData;
    }

    private Grid<EmmaData> getGridData(Calendar startOfWeekCalender, Calendar endOfWeekCalender, String errorMessage) {
        List<EmmaData> emmaDataList = new ArrayList<>();
        Map<String, EmmaData> emmaDataMap = new HashMap<>();
        Map<String, EmmaData> tempEmmaDataMap = null;
        Grid<EmmaData> grid = EmmaComponentUtil.getGrid();
        while (startOfWeekCalender.before(endOfWeekCalender)) {
            tempEmmaDataMap = fireBaseService.getCollectionMapData(dateFormat.format(startOfWeekCalender.getTime()));
            if (tempEmmaDataMap != null && !tempEmmaDataMap.isEmpty()) {
                tempEmmaDataMap.entrySet().forEach(entry -> emmaDataMap.put(entry.getKey(), entry.getValue()));
            }
            startOfWeekCalender.add(Calendar.DATE, 1);
        }
        if (!emmaDataMap.isEmpty()) {
            emmaDataList = emmaDataMap.entrySet().stream().map(Map.Entry::getValue).toList();
        }
        if (emmaDataList.isEmpty()) {
            EmmaComponentUtil.getNotification(errorMessage, true).open();
        }
        grid.setItems(sortEmmaDataByPercentageChange(new ArrayList<>(emmaDataList)));
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

    private List<EmmaData> sortEmmaDataByPercentageChange(List<EmmaData> emmaDataList) {
        AtomicReference<Float> emmaData1PercentageChange = new AtomicReference<>(0F);
        AtomicReference<Float> emmaData2PercentageChange = new AtomicReference<>(0F);
        Collections.sort(emmaDataList, (emmaData1, emmaData2) -> {
            emmaData1PercentageChange.set(Float.valueOf(emmaData1.getPercentageChange()));
            emmaData2PercentageChange.set(Float.valueOf(emmaData2.getPercentageChange()));
            if (emmaData1PercentageChange.get() > emmaData2PercentageChange.get()) {
                return -1;
            }
            if (emmaData1PercentageChange.get() < emmaData2PercentageChange.get()) {
                return 1;
            }
            return 0;
        });
        return emmaDataList;
    }

    private List<EmmaFrequencyData> sortEmmaFrequencyDataByOccurrence(List<EmmaFrequencyData> frequencyDataList) {
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
