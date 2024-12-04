package com.sonar.vishal.emma.task;

import com.sonar.vishal.emma.bus.LogErrorEvent;
import com.sonar.vishal.emma.entity.Data;
import com.sonar.vishal.emma.service.FireBaseService;
import com.sonar.vishal.emma.util.Constant;
import com.sonar.vishal.emma.util.TaskUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Profile("TASK")
public class DataAcquisitionTask {

    @Autowired
    private FireBaseService fireBaseService;

    @Value("${application.scheduler.url}")
    private String url;

    @Value("${application.scheduler.html.widget}")
    private String htmlWidget;

    @Value("${application.scheduler.html.numberFonts}")
    private String numberFonts;

    @Scheduled(fixedRateString = "${application.scheduler.fixedRate.millisecond}")
    public void execute() {
        if (!TaskUtil.inBusinessHour()) {
            return;
        }
        try {
            List<Data> dataList = new ArrayList<>();
            AtomicInteger counter = new AtomicInteger();
            ChromeOptions options = new ChromeOptions();
            options.addArguments(Constant.CHROME_OPTION_HEADLESS);
            ChromeDriver chromeDriver = new ChromeDriver(options);
            chromeDriver.get(url);
            Thread.sleep(10000);
            WebElement widget = chromeDriver.findElement(By.className(htmlWidget));
            List<String> newLineList = Arrays.asList(widget.getText().split(Constant.NEW_LINE_REGEX));

            newLineList.subList(3, 55).stream().collect(Collectors.groupingBy(it -> counter.getAndIncrement() / 2)).values()
                    .stream().map(list -> convertIteratorToData((list.iterator()))).forEach(dataList::add);

            WebElement dateWebElement = chromeDriver.findElement(By.className(numberFonts));
            String[] parseDateArray = dateWebElement.getText().split(Constant.SPACE_REGEX);
            String documentName = getDocumentName(parseDateArray);
            fireBaseService.addOrUpdateDocument(dataList, documentName);
            fireBaseService.updateTaskStatus(Constant.DATA_AQUISITION_TASK_NAME);
            chromeDriver.close();
        } catch (InterruptedException interruptedException) {
            Constant.LOG_EVENT_BUS.post(new LogErrorEvent().setMessage("DataAcquisitionTask :: execute :: Thread Interrupted Exception.").setException(interruptedException));
            Thread.currentThread().interrupt();
        } catch (Exception exception) {
            Constant.LOG_EVENT_BUS.post(new LogErrorEvent().setMessage("DataAcquisitionTask :: execute :: Error while executing task.").setException(exception));
        }
    }

    private String getDocumentName(String[] parseDateArray) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constant.DOCUMENT_PARSE_DATE_FORMAT_PATTERN);
        Date parseDate = dateFormat.parse(parseDateArray[2] + Constant.HYPHEN + parseDateArray[3] + Constant.HYPHEN + parseDateArray[4]);
        dateFormat = new SimpleDateFormat(Constant.DOCUMENT_DATE_FORMAT_PATTERN);
        return dateFormat.format(parseDate);
    }

    private Data convertIteratorToData(Iterator<String> iterator) {
        Data data = new Data();
        data.setCompanyName(iterator.next());
        String[] price = iterator.next().split(Constant.SPACE_REGEX);
        data.setLastTradePrice(price[0].replace(Constant.COMMA, Constant.EMPTY));
        data.setPercentageChange(price[1].split(Constant.PERCENTAGE_REGEX)[0]);
        return data;
    }
}
