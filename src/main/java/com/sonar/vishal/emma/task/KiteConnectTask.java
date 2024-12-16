package com.sonar.vishal.emma.task;

import com.sonar.vishal.emma.algorithm.OneGainAlgorithm;
import com.sonar.vishal.emma.algorithm.TradeAlgorithm;
import com.sonar.vishal.emma.bus.OrderEventListener;
import com.sonar.vishal.emma.context.Context;
import com.sonar.vishal.emma.entity.Data;
import com.sonar.vishal.emma.enumeration.ThreadStatus;
import com.sonar.vishal.emma.service.FireBaseService;
import com.sonar.vishal.emma.service.KiteConnectService;
import com.sonar.vishal.emma.util.Constant;
import com.sonar.vishal.emma.util.TaskUtil;
import com.sonar.vishal.emma.util.TradeAlgorithmMap;
import jakarta.annotation.PostConstruct;
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

    private String collectionName;
    private List<String> companyName;
    private Map<String, Object> companyNameData;
    private boolean updateCompanyNameMap = false;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Value("${application.kite.connect.list.size}")
    private int companyNameListSize;

    @Autowired
    private FireBaseService fireBaseService;

    @Autowired
    private KiteConnectService kiteConnectService;

    @Autowired
    private OrderEventListener orderEventListener;

    @PostConstruct
    public void init() {
        SimpleDateFormat dateFormat = Context.getBean(SimpleDateFormat.class, Constant.DOCUMENT_DATE_FORMAT_PATTERN);
        collectionName = dateFormat.format(Context.getBean(Date.class));
        kiteConnectService.login();
        if (kiteConnectService.isConnectionOpen()) {
            kiteConnectService.updateOrder();
            TradeAlgorithmMap.ORDER_EVENT_BUS.register(orderEventListener);
        }
    }

    @Scheduled(fixedRateString = "${application.kite.connect.fixedRate.millisecond}")
    public void execute() {
        if (TaskUtil.inBusinessHour() && kiteConnectService.isConnectionOpen()) {
            updateCompanyNameList();
            companyName.stream().filter(name -> TradeAlgorithmMap.TRADE_STATUS.get(name) == null || TradeAlgorithmMap.TRADE_STATUS.get(name).equals(ThreadStatus.DEAD))
                    .forEach(name -> executorService.execute(executeAlgorithm(name)));
        }
    }

    private TradeAlgorithm executeAlgorithm(String companyName) {
        ArrayList<Long> tokens = Context.getBean(ArrayList.class);
        tokens.addAll(List.of(TradeAlgorithmMap.NSE_TRADE_TOKEN.get(companyName), TradeAlgorithmMap.BSE_TRADE_TOKEN.get(companyName)));
        kiteConnectService.subscribe(tokens);

        TradeAlgorithm tradeAlgorithm = Context.getBean(OneGainAlgorithm.class, companyName);
        return tradeAlgorithm;
    }

    private void updateCompanyNameList() {
        List<Data> dataList = Context.getBean(ArrayList.class);
        dataList.addAll(fireBaseService.getCollectionMapData(collectionName).values());
        Collections.sort(dataList, (data1, data2) -> compareDouble(data1.getPercentageChange(), data2.getPercentageChange()));
        if (dataList.size() > companyNameListSize) {
            dataList = dataList.subList(0, companyNameListSize);
        }
        companyName = dataList.stream().map(Data::getCompanyName).map(this::mapCompanyName).filter(item -> !item.equals(Constant.EMPTY)).toList();
        if (updateCompanyNameMap) {
            fireBaseService.setCompanyNameData(companyNameData);
        }
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
