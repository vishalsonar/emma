package com.sonar.vishal.emma.algorithm;

import com.sonar.vishal.emma.bus.LogErrorEvent;
import com.sonar.vishal.emma.context.Context;
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

    protected TradeAlgorithm(String companyName) {
        this.companyName = companyName;
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

    protected abstract void executeAlgorithm();

    protected abstract boolean canExecuteAlgorithm();

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

    protected void handleException(ThreadStatus status) {
        TradeAlgorithmMap.TRADE_STATUS.put(companyName, status);
        this.companyName = null;
        this.orderParams = null;
    }

    @Override
    public void run() {
        try {
            while (TradeAlgorithmMap.TRADE_STATUS.get(companyName).equals(ThreadStatus.ALIVE)) {
                if (TaskUtil.inBusinessHour()) {
                    if (canExecuteAlgorithm()) {
                        executeAlgorithm();
                    }
                } else {
                    TradeAlgorithmMap.TRADE_STATUS.get(companyName).equals(ThreadStatus.CANCELED);
                    Constant.LOG_EVENT_BUS.post(Context.getBean(LogErrorEvent.class).setMessage("Trade Thread :: " + companyName + " :: stopped trading.").setException(new Exception("Thread Exit Trade.")));
                    break;
                }
            }
        } catch (Exception exception) {
            TradeAlgorithmMap.TRADE_STATUS.put(companyName, ThreadStatus.DEAD);
            Constant.LOG_EVENT_BUS.post(Context.getBean(LogErrorEvent.class).setMessage(getClass().getName() + " :: run :: " + companyName + " :: Error executing trade.").setException(exception));
        }
    }
}
