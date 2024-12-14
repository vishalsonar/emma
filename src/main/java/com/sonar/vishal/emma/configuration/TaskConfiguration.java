package com.sonar.vishal.emma.configuration;

import com.vaadin.flow.spring.annotation.UIScope;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

@Configuration
@Profile("TASK")
public class TaskConfiguration {

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ChromeOptions getChromeOptions() {
        return new ChromeOptions();
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ChromeDriver getChromeDriver(ChromeOptions options) {
        return new ChromeDriver(options);
    }

}
