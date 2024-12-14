package com.sonar.vishal.emma.bus;

import com.google.common.eventbus.Subscribe;
import com.sonar.vishal.emma.util.Constant;
import com.sonar.vishal.emma.util.TradeAlgorithmMap;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private KiteConnect kiteConnect;

    public void setKiteConnect(KiteConnect kiteConnect) {
        this.kiteConnect = kiteConnect;
    }

    @Subscribe
    public void execute(OrderParams orderParams) {
        try {
            Order order = kiteConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
            TradeAlgorithmMap.TRADE_ORDER_STATUS.put(orderParams.tradingsymbol, order);
        } catch (KiteException kiteException) {
            Constant.LOG_EVENT_BUS.post(new LogErrorEvent().setMessage("OrderEventListener :: execute :: " + kiteException.message).setException(kiteException));
        } catch (Exception exception) {
            Constant.LOG_EVENT_BUS.post(new LogErrorEvent().setMessage("OrderEventListener :: execute :: Error while executing order.").setException(exception));
        }
    }
}
