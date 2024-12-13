package com.sonar.vishal.emma.listener;

import com.sonar.vishal.emma.util.TradeAlgorithmMap;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.OnTicks;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class TradeOnTickerArrivalListener implements OnTicks {

    @Override
    public void onTicks(ArrayList<Tick> ticks) {
        ticks.stream().forEach(tick -> TradeAlgorithmMap.TRADE_PRICE.put(tick.getInstrumentToken(), tick.getLastTradedPrice()));
    }
}
