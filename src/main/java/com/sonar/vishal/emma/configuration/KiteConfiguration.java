package com.sonar.vishal.emma.configuration;

import com.vaadin.flow.spring.annotation.UIScope;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.models.OrderParams;
import com.zerodhatech.ticker.KiteTicker;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class KiteConfiguration {

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public OrderParams getOrderParams() {
        return new OrderParams();
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public KiteConnect getKiteConnect(String apiKey) {
        return new KiteConnect(apiKey);
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public KiteTicker getKiteTicker(String accessToken, String apiKey) {
        return new KiteTicker(accessToken, apiKey);
    }
}
