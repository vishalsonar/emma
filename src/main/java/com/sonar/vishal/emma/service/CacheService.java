package com.sonar.vishal.emma.service;

import com.google.common.cache.CacheLoader;
import com.sonar.vishal.emma.context.Context;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CacheService<A, B, C> extends CacheLoader<A, Map<B, C>> {

    private Function<A, Map<B, C>> function;

    public void setFunction(Function<A, Map<B, C>> function) {
        this.function = function;
    }

    @Override
    public Map<B, C> load(A key) throws Exception {
        Map<B, C> map = function.apply(key);
        if (map == null) {
            map = Context.getBean(HashMap.class);
        }
        return map;
    }
}
