package com.sonar.vishal.emma.algorithm;

import com.sonar.vishal.emma.bus.LogErrorEvent;
import com.sonar.vishal.emma.context.Context;
import com.sonar.vishal.emma.enumeration.AlgorithmState;
import com.sonar.vishal.emma.enumeration.ThreadStatus;
import com.sonar.vishal.emma.util.Constant;
import com.sonar.vishal.emma.util.TaskUtil;
import com.sonar.vishal.emma.util.TradeAlgorithmMap;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.OrderParams;

public abstract class TradeAlgorithm implements Runnable {

    protected Long bseToken;
    protected Long nseToken;
    protected String companyName;
    protected OrderParams orderParams;
    protected AlgorithmState transactionState;

    protected TradeAlgorithm(String companyName) {
        this.companyName = companyName;
        this.transactionState = AlgorithmState.ACCEPTING;
        this.nseToken = TradeAlgorithmMap.NSE_TRADE_TOKEN.get(companyName);
        this.bseToken = TradeAlgorithmMap.BSE_TRADE_TOKEN.get(companyName);
        TradeAlgorithmMap.TRADE_STATUS.put(companyName, ThreadStatus.ALIVE);
    }

    protected abstract int getBuyQuantity();

    protected abstract int getSellQuantity();

    protected abstract double getBuyPrice();

    protected abstract double getSellPrice();

    protected abstract void submitBuyOrder();

    protected abstract void submitSellOrder();

    protected abstract void submitAccounting();

    protected abstract void submitAccepting();

    protected abstract void nextState();

    protected abstract boolean isExecutable();

    protected void updateToDefaultOrder() {
        orderParams = Context.getBean(OrderParams.class);
        orderParams.quantity = 0;
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.tradingsymbol = companyName;
        orderParams.product = Constants.PRODUCT_CNC;
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = 0.0;
        orderParams.triggerPrice = 0.0;
    }

    @Override
    public void run() {
        try {
            while (TradeAlgorithmMap.TRADE_STATUS.get(companyName).equals(ThreadStatus.ALIVE)) {
                if (!TaskUtil.inBusinessHour()) {
                    TradeAlgorithmMap.TRADE_STATUS.get(companyName).equals(ThreadStatus.NOT_IN_BUSINESS_HOUR);
                    break;
                }
                if (TradeAlgorithmMap.TRADE_PRICE.containsKey(nseToken)) {
                    if (isExecutable()) {
                        switch (this.transactionState) {
                            case ACCEPTING -> submitAccepting();
                            case BUYING -> submitBuyOrder();
                            case SELLING -> submitSellOrder();
                            case ACCOUNTING -> submitAccounting();
                        }
                    }
                    nextState();
                }
            }
        } catch (Exception exception) {
            TradeAlgorithmMap.TRADE_STATUS.put(companyName, ThreadStatus.EXCEPTION);
            Constant.LOG_EVENT_BUS.post(Context.getBean(LogErrorEvent.class).setMessage(getClass().getName() + " :: run :: " + companyName + " :: Error executing trade.").setException(exception));
        }
        Constant.LOG_EVENT_BUS.post(Context.getBean(LogErrorEvent.class).setMessage("Trade Thread :: " + companyName + " :: stopped trading.").setException(Context.getBean(Exception.class, TradeAlgorithmMap.TRADE_STATUS.get(companyName))));
    }
}
