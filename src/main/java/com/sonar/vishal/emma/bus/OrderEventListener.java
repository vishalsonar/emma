package com.sonar.vishal.emma.bus;

import com.google.common.eventbus.Subscribe;
import com.sonar.vishal.emma.context.Context;
import com.sonar.vishal.emma.service.KiteConnectService;
import com.sonar.vishal.emma.util.Constant;
import com.sonar.vishal.emma.util.TradeAlgorithmMap;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.OrderParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    @Autowired
    private KiteConnectService kiteConnectService;

    @Subscribe
    public void execute(OrderParams orderParams) {
        try {
            if (kiteConnectService.isConnectionOpen()) {
                String orderId = kiteConnectService.getKiteConnect().placeOrder(orderParams, Constants.VARIETY_REGULAR).orderId;
                TradeAlgorithmMap.LAST_TRADE_ORDER.put(orderParams.tradingsymbol, orderId);
                kiteConnectService.updateOrder();
            }
        } catch (KiteException kiteException) {
            Constant.LOG_EVENT_BUS.post(Context.getBean(LogErrorEvent.class).setMessage("OrderEventListener :: execute :: " + kiteException.message).setException(kiteException));
        } catch (Exception exception) {
            Constant.LOG_EVENT_BUS.post(Context.getBean(LogErrorEvent.class).setMessage("OrderEventListener :: execute :: Error while executing order.").setException(exception));
        }
    }
}
