package com.sonar.vishal.emma.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.sonar.vishal.emma.bus.LogErrorEvent;
import com.sonar.vishal.emma.entity.Data;
import com.sonar.vishal.emma.util.Constant;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class FireBaseService implements Serializable {

    private static Firestore firestore;
    private static LoadingCache<String, Map<String, Data>> dataCache;
    private static LoadingCache<String, Map<String, Object>> frequencyCache;

    @Value("${application.data.cache.maximumSize}")
    private String dataMaximumSize;

    @Value("${application.data.cache.expireAfterWrite.minutes}")
    private String dataExpiryMinutes;

    @Value("${application.frequency.cache.maximumSize}")
    private String frequencyMaximumSize;

    @Value("${application.frequency.cache.expireAfterWrite.minutes}")
    private String frequencyExpiryMinutes;

    @Autowired
    private DataCacheService dataCacheService;

    @Autowired
    private FrequencyCacheService frequencyCacheService;

    static {
        try {
            if (firestore == null) {
                InputStream serviceAccount = FireBaseService.class.getClassLoader().getResourceAsStream(Constant.SERVICE_ACCOUNT_FILE_NAME);
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).build();
                FirebaseApp.initializeApp(options);
                firestore = FirestoreClient.getFirestore();
            }
        } catch (Exception exception) {
            Constant.eventBus.post(new LogErrorEvent().setMessage("FireBaseService :: static :: Error while initializing account.").setException(exception));
        }
    }

    @PostConstruct
    private void postConstruct() {
        if (dataCache == null) {
            dataCacheService.setFirestore(firestore);
            dataCache = CacheBuilder.newBuilder().maximumSize(Integer.parseInt(dataMaximumSize)).expireAfterWrite(Integer.parseInt(dataExpiryMinutes), TimeUnit.MINUTES).build(dataCacheService);
        }
        if (frequencyCache == null) {
            frequencyCacheService.setFirestore(firestore);
            frequencyCache = CacheBuilder.newBuilder().maximumSize(Integer.parseInt(frequencyMaximumSize)).expireAfterWrite(Integer.parseInt(frequencyExpiryMinutes), TimeUnit.MINUTES).build(frequencyCacheService);
        }
    }

    public void addOrUpdateDocument(List<Data> dataList, String documentName) {
        try {
            dataList.stream().forEach(data -> firestore.collection(documentName).document(data.getCompanyName()).set(data));
            Map<String, String> dataListMap = dataList.stream().collect(Collectors.toMap(Data::getCompanyName, Data::getPercentageChange));
            Map<String, Object> remoteDataListMap = firestore.collection(Constant.ANALYTICS).document(documentName).get().get().getData();
            if (remoteDataListMap != null) {
                remoteDataListMap.entrySet().forEach(entry -> {
                    if (!dataListMap.containsKey(entry.getKey())) {
                        dataListMap.put(entry.getKey(), entry.getValue().toString());
                    }
                });
            }
            firestore.collection(Constant.ANALYTICS).document(documentName).set(dataListMap);
        } catch (Exception exception) {
            Constant.eventBus.post(new LogErrorEvent().setMessage("FireBaseService :: addOrUpdateDocument :: Unable to add or udpate document.").setException(exception));
        }
    }

    public Map<String, Data> getCollectionMapData(String collectionName) {
        Map<String, Data> dataMap = new HashMap<>();
        try {
            dataMap = dataCache.get(collectionName);
        } catch (Exception exception) {
            Constant.eventBus.post(new LogErrorEvent().setMessage("FireBaseService :: getCollectionMapData :: Cache read exception.").setException(exception));
        }
        return dataMap;
    }

    public Map<String, Object> getFrequencyData() {
        Map<String, Object> frequencyData = new HashMap<>();
        try {
            frequencyData = frequencyCache.get(Constant.EMPTY);
        } catch (Exception exception) {
            Constant.eventBus.post(new LogErrorEvent().setMessage("FireBaseService :: getFrequencyData :: Failed to update Frequency.").setException(exception));
        }
        return frequencyData;
    }

    public void mergeFrequency(String documentName) {
        try {
            AtomicReference<Long> count = new AtomicReference<>();
            AtomicReference<String> averageFrequency = new AtomicReference<>();
            Map<String, Object> frequencyData = getFrequencyData();
            Map<String, Object> remoteDataListMap = firestore.collection(Constant.ANALYTICS).document(documentName).get().get().getData();
            Map<String, Object> newFrequencydata = new HashMap<>();
            if (remoteDataListMap == null) {
                return;
            }
            remoteDataListMap.entrySet().forEach(entry -> {
                if (frequencyData.containsKey(entry.getKey())) {
                    String[] valueString = frequencyData.get(entry.getKey()).toString().split(Constant.PIPE_REGEX);
                    count.set((Long.parseLong(valueString[0]) + 1));
                    averageFrequency.set(String.valueOf((Double.parseDouble(valueString[1]) + Double.parseDouble(entry.getValue().toString())) / 2.0d));
                } else {
                    count.set(1L);
                    averageFrequency.set(entry.getValue().toString());
                }
                newFrequencydata.put(entry.getKey(), count.get() + Constant.PIPE + averageFrequency.get());
            });
            firestore.collection(Constant.ANALYTICS).document(Constant.FREQUENCY).set(newFrequencydata);
            firestore.collection(Constant.ANALYTICS).document(documentName).delete();
        } catch (Exception exception) {
            Constant.eventBus.post(new LogErrorEvent().setMessage("FireBaseService :: mergeFrequency :: Failed to merge Frequency.").setException(exception));
        }
    }
}
