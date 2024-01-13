package com.sonar.vishal.emma.service;

import com.google.cloud.firestore.Firestore;
import com.google.common.cache.CacheLoader;
import com.sonar.vishal.emma.util.Constant;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FrequencyCacheService extends CacheLoader<String, Map<String, Object>> {

    private Firestore firestore;

    public void setFirestore(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Map<String, Object> load(String key) throws Exception {
        Map<String, Object> frequencyData = null;
        try {
            frequencyData = firestore.collection(Constant.ANALYTICS).document(Constant.FREQUENCY).get().get().getData();
            if (frequencyData == null) {
                frequencyData = new HashMap<>();
            }
        } catch (Exception exception) {
            Constant.LOG.error("FrequencyCacheService :: load :: Error while loading cache data.", exception);
        }
        return frequencyData;
    }
}
