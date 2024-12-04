package com.sonar.vishal.emma.task;

import com.sonar.vishal.emma.bus.OrderEventListener;
import com.sonar.vishal.emma.service.KiteConnectService;
import com.sonar.vishal.emma.util.Constant;
import com.zerodhatech.kiteconnect.KiteConnect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("KITE")
public class KiteConnectTask {

    @Autowired
    private KiteConnectService kiteConnectService;

    @Scheduled(fixedRateString = "${application.kite.connect.fixedRate.millisecond}")
    public void execute() {
        KiteConnect kiteConnect = kiteConnectService.getKiteConnect();
        if (kiteConnect == null) {
            kiteConnectService.login();
            kiteConnect = kiteConnectService.getKiteConnect();
        }
        if (kiteConnect != null) {
            Constant.ORDER_EVENT_BUS.register(new OrderEventListener(kiteConnect));
        }
    }
}
