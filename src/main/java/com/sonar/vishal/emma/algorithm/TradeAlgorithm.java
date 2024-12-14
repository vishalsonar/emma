package com.sonar.vishal.emma.algorithm;

import com.sonar.vishal.emma.context.Context;
import com.sonar.vishal.emma.enumeration.ThreadStatus;
import com.sonar.vishal.emma.util.TradeAlgorithmMap;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;

public abstract class TradeAlgorithm implements Runnable {

    protected boolean isBuy;
    protected String companyName;
    protected OrderParams orderParams;

    public TradeAlgorithm(String companyName) {
        this.isBuy = true;
        this.companyName = companyName;
        TradeAlgorithmMap.TRADE_STATUS.put(companyName, ThreadStatus.ALIVE);
    }

    protected abstract int calculateQuantity();

    protected abstract double calculateBuyPrice();

    protected abstract double calculateSellPrice();

    protected void submitOrder() {
        orderParams = Context.getBean(OrderParams.class);
        orderParams.quantity = calculateQuantity();
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.tradingsymbol = companyName;
        orderParams.product = Constants.PRODUCT_CNC;
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.transactionType = isBuy ? Constants.TRANSACTION_TYPE_BUY : Constants.TRANSACTION_TYPE_SELL;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = isBuy ? calculateBuyPrice() : calculateSellPrice();
        orderParams.triggerPrice = 0.0;
        TradeAlgorithmMap.ORDER_EVENT_BUS.post(orderParams);
    }

    protected boolean isLastOrderExecuted() {
        Order order = TradeAlgorithmMap.TRADE_ORDER_STATUS.get(companyName);
        return order == null ? Boolean.TRUE : order.status.equals(Constants.ORDER_COMPLETE);
    }

    protected void handleException(ThreadStatus status) {
        TradeAlgorithmMap.TRADE_STATUS.put(companyName, status);
        this.isBuy = true;
        this.companyName = null;
        this.orderParams = null;
    }
}
