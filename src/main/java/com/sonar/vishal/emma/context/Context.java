package com.sonar.vishal.emma.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class Context implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static synchronized <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static synchronized <T> T getBean(Class<T> requiredType, Object... args) {
        return applicationContext.getBean(requiredType, args);
    }

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Context.applicationContext = applicationContext;
    }
}
