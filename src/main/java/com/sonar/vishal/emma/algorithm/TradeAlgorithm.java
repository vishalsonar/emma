package com.sonar.vishal.emma.algorithm;

import com.sonar.vishal.emma.enumeration.ThreadStatus;
import com.sonar.vishal.emma.util.Constant;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TradeAlgorithm implements Runnable {

    public static final Map<String, Order> TRADE_ORDER_STATUS = new ConcurrentHashMap<>();
    public static final Map<String, ThreadStatus> TRADE_STATUS = new ConcurrentHashMap<>();

    public static TradeAlgorithm getInstance(String companyName) {
        TradeAlgorithm tradeAlgorithm = new OneGainAlgorithm();
        tradeAlgorithm.initialize(companyName);
        return tradeAlgorithm;
    }

    protected boolean isBuy;
    protected String companyName;
    protected OrderParams orderParams;

    protected abstract int calculateQuantity();

    protected abstract double calculateBuyPrice();

    protected abstract double calculateSellPrice();

    protected void submitOrder() {
        orderParams = new OrderParams();
        orderParams.quantity = calculateQuantity();
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.tradingsymbol = companyName;
        orderParams.product = Constants.PRODUCT_CNC;
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.transactionType = isBuy ? Constants.TRANSACTION_TYPE_BUY : Constants.TRANSACTION_TYPE_SELL;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = isBuy ? calculateBuyPrice() : calculateSellPrice();
        orderParams.triggerPrice = 0.0;
        Constant.ORDER_EVENT_BUS.post(orderParams);
    }

    protected boolean isLastOrderExecuted() {
        Order order = TRADE_ORDER_STATUS.get(companyName);
        return order == null ? Boolean.TRUE : order.status.equals(Constants.ORDER_COMPLETE);
    }

    protected void initialize(String companyName) {
        this.isBuy = true;
        this.companyName = companyName;
        TradeAlgorithm.TRADE_STATUS.put(companyName, ThreadStatus.ALIVE);
    }

    protected void handleException(ThreadStatus status) {
        TradeAlgorithm.TRADE_STATUS.put(companyName, status);
        this.isBuy = true;
        this.companyName = null;
        this.orderParams = null;
    }
}
