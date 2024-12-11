package com.sonar.vishal.emma.service;

import com.google.common.cache.CacheLoader;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class CacheService<A, B, C> extends CacheLoader<A, Map<B, C>> {

    private Function<A, Map<B, C>> function;

    public void setFunction(Function<A, Map<B, C>> function) {
        this.function = function;
    }

    @Override
    public Map<B, C> load(A key) throws Exception {
        Map<B, C> map = function.apply(key);
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }
}
