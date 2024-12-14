package com.sonar.vishal.emma.configuration;

import com.sonar.vishal.emma.util.Constant;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

import java.util.Date;

@Configuration
@Profile("ADMIN")
public class AdminConfiguration {

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TabSheet getTabSheet() {
        return new TabSheet();
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Html getHtml() {
        return new Html(Constant.HTML_HR);
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Span getSpan(String logo) {
        return new Span(logo);
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Date getDate() {
        return new Date();
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public <T> Grid<T> getGrid(Class<T> beanType, boolean autoCreateColumns) {
        return new Grid<>(beanType, autoCreateColumns);
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Notification getNotification(String message) {
        return new Notification(message);
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public <T> Binder<T> getBinder(Class<T> beanType) {
        return new Binder<>(beanType);
    }

    @Bean
    @UIScope
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TextField getTextField() {
        return new TextField();
    }

}
