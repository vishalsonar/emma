package com.sonar.vishal.emma.task;

import com.sonar.vishal.emma.entity.EmmaData;
import com.sonar.vishal.emma.service.EmmaFireBaseService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class EmmaDataAcquisitionTask {

    private static final Logger LOG = LoggerFactory.getLogger(EmmaDataAcquisitionTask.class);
    private static final String NEW_LINE_REGEX = "\n";
    private static final String CHROME_OPTION_HEADLESS = "--headless";

    @Autowired
    private EmmaFireBaseService fireBaseService;

    @Value("${application.scheduler.url}")
    private String url;

    @Value("${application.scheduler.html.widget}")
    private String htmlWidget;

    @Scheduled(fixedRateString = "${application.scheduler.fixedRate.millisecond}")
    public void execute() {
        if (!inBusinessHour()) {
            return;
        }
        try {
            List<EmmaData> dataList = new ArrayList<>();
            AtomicInteger counter = new AtomicInteger();
            ChromeOptions options = new ChromeOptions();
            options.addArguments(CHROME_OPTION_HEADLESS);
            ChromeDriver chromeDriver = new ChromeDriver(options);
            chromeDriver.get(url);
            Thread.sleep(10000);
            WebElement widget = chromeDriver.findElement(By.className(htmlWidget));
            List<String> newLineList = Arrays.asList(widget.getText().split(NEW_LINE_REGEX));

            newLineList.subList(6, newLineList.size()).stream().collect(Collectors.groupingBy(it -> counter.getAndIncrement() / 7)).values()
                    .stream().map(list -> convertIteratorToEmmaData((list.iterator()))).forEach(dataList::add);

            fireBaseService.addOrUpdateDocument(dataList);
            chromeDriver.close();
        } catch (InterruptedException interruptedException) {
            LOG.error("EmmaScheduledTask :: execute :: Thread Interrupted Exception.", interruptedException);
            Thread.currentThread().interrupt();
        } catch (Exception exception) {
            LOG.error("EmmaScheduledTask :: execute :: Error while executing task.", exception);
        }
    }

    private EmmaData convertIteratorToEmmaData(Iterator<String> iterator) {
        EmmaData data = new EmmaData();
        data.setCompanyName(iterator.next());
        data.setLastTradePrice(iterator.next());
        data.setChange(iterator.next());
        data.setPercentageChange(iterator.next());
        data.setVolume(iterator.next());
        data.setDayLow(iterator.next());
        data.setDayHigh(iterator.next());
        return data;
    }

    private boolean inBusinessHour() {
        boolean state = false;
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && currentHour >= 9 && currentHour < 16) {
            state = true;
        }
        return state;
    }
}
