package com.sonar.vishal.emma.service;

import com.google.cloud.firestore.Firestore;
import com.google.common.cache.CacheLoader;
import com.sonar.vishal.emma.bus.LogErrorEvent;
import com.sonar.vishal.emma.entity.Data;
import com.sonar.vishal.emma.util.Constant;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DataCacheService extends CacheLoader<String, Map<String, Data>> {

    private Firestore firestore;

    public void setFirestore(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Map<String, Data> load(String collectionName) throws Exception {
        Map<String, Data> dataMap = new HashMap<>();
        try {
            firestore.collection(collectionName).get().get().getDocuments().forEach(document -> dataMap.put(document.getId(), document.toObject(Data.class)));
        } catch (Exception exception) {
            Constant.eventBus.post(new LogErrorEvent().setMessage("DataCacheService :: load :: Error while loading cache data.").setException(exception));
        }
        return dataMap;
    }
}
