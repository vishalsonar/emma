package com.sonar.vishal.emma.configuration;

import com.google.common.eventbus.EventBus;
import com.sonar.vishal.emma.bus.LogErrorEvent;
import com.sonar.vishal.emma.entity.CompanyNameData;
import com.sonar.vishal.emma.entity.Data;
import com.sonar.vishal.emma.entity.FrequencyData;
import com.sonar.vishal.emma.entity.TaskData;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public EventBus getEventBus(String identifier) {
        return new EventBus(identifier);
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HashMap<?, ?> getHashMap() {
        return new HashMap<>();
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ConcurrentHashMap<?, ?> getConcurrentHashMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ArrayList<?> getArrayList() {
        return new ArrayList<>();
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AtomicInteger getAtomicInteger() {
        return new AtomicInteger();
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public LogErrorEvent getLogErrorEvent() {
        return new LogErrorEvent();
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SimpleDateFormat getSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Data getData() {
        return new Data();
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CompanyNameData getCompanyNameData() {
        return new CompanyNameData();
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public FrequencyData getFrequencyData() {
        return new FrequencyData();
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TaskData getTaskData() {
        return new TaskData();
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AtomicReference<?> getAtomicReference() {
        return new AtomicReference<>();
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ByteArrayInputStream getByteArrayInputStream(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Exception getException(String exceptionMessage) {
        return new Exception(exceptionMessage);
    }
}
