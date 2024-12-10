package com.sonar.vishal.emma.algorithm;

import org.springframework.stereotype.Service;

@Service
public class OneGainAlgorithm extends TradeAlgorithm {

    @Override
    public void run() {
        System.out.println(companyName);
    }

    @Override
    protected int calculateQuantity() {
        return 0;
    }

    @Override
    protected double calculateBuyPrice() {
        return 0.0;
    }

    @Override
    protected double calculateSellPrice() {
        return 0.0;
    }
}
