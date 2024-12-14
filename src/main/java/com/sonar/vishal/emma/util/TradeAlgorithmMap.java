package com.sonar.vishal.emma.util;

import com.google.common.eventbus.EventBus;
import com.sonar.vishal.emma.context.Context;
import com.sonar.vishal.emma.enumeration.ThreadStatus;
import com.zerodhatech.models.Order;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Profile("KITE")
public class TradeAlgorithmMap {

    private TradeAlgorithmMap() {
        // Prevent Instantiation
    }

    public static final EventBus ORDER_EVENT_BUS = Context.getBean(EventBus.class, "ORDER EVENT BUS");

    public static final Map<Long, Double> TRADE_PRICE = Context.getBean(ConcurrentHashMap.class);
    public static final Map<String, Long> NSE_TRADE_TOKEN = Context.getBean(ConcurrentHashMap.class);
    public static final Map<String, Long> BSE_TRADE_TOKEN = Context.getBean(ConcurrentHashMap.class);
    public static final Map<String, Order> TRADE_ORDER_STATUS = Context.getBean(ConcurrentHashMap.class);
    public static final Map<String, ThreadStatus> TRADE_STATUS = Context.getBean(ConcurrentHashMap.class);
}
