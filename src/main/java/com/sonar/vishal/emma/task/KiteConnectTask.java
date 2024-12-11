package com.sonar.vishal.emma.task;

import com.sonar.vishal.emma.algorithm.TradeAlgorithm;
import com.sonar.vishal.emma.bus.OrderEventListener;
import com.sonar.vishal.emma.entity.Data;
import com.sonar.vishal.emma.enumeration.ThreadStatus;
import com.sonar.vishal.emma.service.FireBaseService;
import com.sonar.vishal.emma.service.KiteConnectService;
import com.sonar.vishal.emma.util.Constant;
import com.zerodhatech.kiteconnect.KiteConnect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Profile("KITE")
public class KiteConnectTask {

    private KiteConnect kiteConnect;
    private SimpleDateFormat dateFormat;
    private boolean isFirstExecution = true;
    private Map<String, Object> companyNameData;
    private boolean updateCompanyNameMap = false;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Value("${application.kite.connect.list.size}")
    private int companyNameListSize;

    @Autowired
    private FireBaseService fireBaseService;

    @Autowired
    private KiteConnectService kiteConnectService;

    public void init() {
        dateFormat = new SimpleDateFormat(Constant.DOCUMENT_DATE_FORMAT_PATTERN);
        kiteConnectService.login();
        kiteConnect = kiteConnectService.getKiteConnect();
        if (kiteConnect != null) {
            Constant.ORDER_EVENT_BUS.register(new OrderEventListener(kiteConnect));
        }
        isFirstExecution = false;
    }

    @Scheduled(fixedRateString = "${application.kite.connect.fixedRate.millisecond}")
    public void execute() {
        if (isFirstExecution) {
            init();
        }
        if (kiteConnect == null) {
            // return;
        }
        List<String> companyName = getCompanyNameList();
        companyName.stream().filter(name -> TradeAlgorithm.TRADE_STATUS.get(name) == null || TradeAlgorithm.TRADE_STATUS.get(name).equals(ThreadStatus.DEAD))
                .forEach(name -> executorService.execute(TradeAlgorithm.getInstance(name)));
        fireBaseService.updateTaskStatus(Constant.KITE_CONNECT_TASK_NAME);
    }

    private List<String> getCompanyNameList() {
        dateFormat = new SimpleDateFormat(Constant.DOCUMENT_DATE_FORMAT_PATTERN);
        List<Data> dataList = new ArrayList<>(fireBaseService.getCollectionMapData(dateFormat.format(new Date())).values());
        Collections.sort(dataList, (data1, data2) -> compareDouble(data1.getPercentageChange(), data2.getPercentageChange()));
        if (dataList.size() > companyNameListSize) {
            dataList = dataList.subList(0, companyNameListSize);
        }
        List<String> companyName = dataList.stream().map(Data::getCompanyName).map(this::mapCompanyName).filter(item -> !item.equals(Constant.EMPTY)).toList();
        if (updateCompanyNameMap) {
            fireBaseService.setCompanyNameData(companyNameData);
        }
        return companyName;
    }

    private String mapCompanyName(String companyName) {
        companyNameData = fireBaseService.getCompanyNameData();
        if (companyNameData.containsKey(companyName)) {
            return companyNameData.get(companyName).toString();
        } else {
            updateCompanyNameMap = true;
            companyNameData.put(companyName, Constant.EMPTY);
            return Constant.EMPTY;
        }
    }

    private int compareDouble(String data1, String data2) {
        return -Double.compare(Double.valueOf(data1), Double.valueOf(data2));
    }
}
