package com.sonar.vishal.emma.util;

import com.sonar.vishal.emma.enumeration.ThreadStatus;
import com.zerodhatech.models.Order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TradeAlgorithmMap {

    public static final Map<Long, Double> TRADE_PRICE = new ConcurrentHashMap<>();
    public static final Map<String, Long> NSE_TRADE_TOKEN = new ConcurrentHashMap<>();
    public static final Map<String, Long> BSE_TRADE_TOKEN = new ConcurrentHashMap<>();
    public static final Map<String, Order> TRADE_ORDER_STATUS = new ConcurrentHashMap<>();
    public static final Map<String, ThreadStatus> TRADE_STATUS = new ConcurrentHashMap<>();
}
