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
import com.sonar.vishal.emma.util.TaskUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
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

    static {
        try {
            if (firestore == null) {
                InputStream serviceAccount = FireBaseService.class.getClassLoader().getResourceAsStream(Constant.SERVICE_ACCOUNT_FILE_NAME);
                if (serviceAccount == null) {
                    serviceAccount = new ByteArrayInputStream(System.getenv(Constant.SYSTEM_SERVICE_ACCOUNT).getBytes());
                }
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).build();
                FirebaseApp.initializeApp(options);
                firestore = FirestoreClient.getFirestore();
                init();
            }
        } catch (Exception exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            Constant.eventBus.post(new LogErrorEvent().setMessage("FireBaseService :: static :: Error while initializing account.").setException(exception));
        }
    }

    private static void init() throws ExecutionException, InterruptedException {
        if (firestore.collection(Constant.ANALYTICS).document(Constant.FREQUENCY).get().get().getData() == null) {
            firestore.collection(Constant.ANALYTICS).document(Constant.FREQUENCY).set(Collections.emptyMap());
        }
        if (firestore.collection(Constant.ANALYTICS).document(Constant.TASK).get().get().getData() == null) {
            firestore.collection(Constant.ANALYTICS).document(Constant.TASK).set(Collections.emptyMap());
        }
    }

    @PostConstruct
    private void postConstruct() {
        if (dataCache == null) {
            CacheService<String, String, Data> dataCacheService = new CacheService<>();
            dataCacheService.setFunction(this::dataCacheFunction);
            dataCache = CacheBuilder.newBuilder().maximumSize(Integer.parseInt(dataMaximumSize)).expireAfterWrite(Integer.parseInt(dataExpiryMinutes), TimeUnit.MINUTES).build(dataCacheService);
        }
        if (frequencyCache == null) {
            CacheService<String, String, Object> frequencyCacheService = new CacheService<>();
            frequencyCacheService.setFunction(this::frequencyCacheFunction);
            frequencyCache = CacheBuilder.newBuilder().maximumSize(Integer.parseInt(frequencyMaximumSize)).expireAfterWrite(Integer.parseInt(frequencyExpiryMinutes), TimeUnit.MINUTES).build(frequencyCacheService);
        }
    }

    private Map<String, Object> frequencyCacheFunction(String collectionName) {
        Map<String, Object> frequencyData = null;
        try {
            frequencyData = firestore.collection(Constant.ANALYTICS).document(Constant.FREQUENCY).get().get().getData();
        } catch (Exception exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            Constant.eventBus.post(new LogErrorEvent().setMessage("FireBaseService :: frequencyCacheFunction :: Error while loading cache data.").setException(exception));
        }
        return frequencyData;
    }

    private Map<String, Data> dataCacheFunction(String collectionName) {
        Map<String, Data> dataMap = new HashMap<>();
        try {
            firestore.collection(collectionName).get().get().getDocuments().forEach(document -> dataMap.put(document.getId(), document.toObject(Data.class)));
        } catch (Exception exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            Constant.eventBus.post(new LogErrorEvent().setMessage("FireBaseService :: dataCacheFunction :: Error while loading cache data.").setException(exception));
        }
        return dataMap;
    }

    public void addOrUpdateDocument(List<Data> dataList, String documentName) {
        try {
            dataList.stream().forEach(data -> firestore.collection(documentName).document(data.getCompanyName()).set(data));
            Map<String, String> dataListMap = dataList.stream().collect(Collectors.toMap(Data::getCompanyName, Data::getPercentageChange));
            Map<String, Object> remoteDataListMap = firestore.collection(Constant.ANALYTICS).document(documentName).get().get().getData();
            if (remoteDataListMap != null) {
                remoteDataListMap.forEach((key, value) -> {
                    if (!dataListMap.containsKey(key)) dataListMap.put(key, String.valueOf(value));
                });
            }
            firestore.collection(Constant.ANALYTICS).document(documentName).set(dataListMap);
        } catch (Exception exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
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
            remoteDataListMap.forEach((key, value) -> {
                count.set(1L);
                averageFrequency.set(String.valueOf(value));
                if (frequencyData.containsKey(key)) {
                    String[] valueString = frequencyData.get(key).toString().split(Constant.PIPE_REGEX);
                    count.set((Long.parseLong(valueString[0]) + 1));
                    Double frequency = (Double.parseDouble(valueString[1]) + Double.parseDouble(String.valueOf(value))) / 2.0d;
                    averageFrequency.set(String.format(Constant.ROUND_DECIMAL_REGEX, frequency));
                }
                newFrequencydata.put(key, count.get() + Constant.PIPE + averageFrequency.get());
            });
            firestore.collection(Constant.ANALYTICS).document(Constant.FREQUENCY).set(newFrequencydata);
            firestore.collection(Constant.ANALYTICS).document(documentName).delete();
        } catch (Exception exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            Constant.eventBus.post(new LogErrorEvent().setMessage("FireBaseService :: mergeFrequency :: Failed to merge Frequency.").setException(exception));
        }
    }

    public void updateTaskStatus(String taskName) {
        try {
            Map<String, Object> remoteTaskListMap = firestore.collection(Constant.ANALYTICS).document(Constant.TASK).get().get().getData();
            if (remoteTaskListMap == null) {
                remoteTaskListMap = new HashMap<>();
            }
            remoteTaskListMap.put(taskName, TaskUtil.getIndiaDateTimeNow());
            firestore.collection(Constant.ANALYTICS).document(Constant.TASK).set(remoteTaskListMap);
        } catch (Exception exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            Constant.eventBus.post(new LogErrorEvent().setMessage("FireBaseService :: updateTaskStatus :: Failed to update Task Status.").setException(exception));
        }
    }

    public Map<String, Object> getTaskStatus() {
        Map<String, Object> remoteTaskListMap = null;
        try {
            remoteTaskListMap = firestore.collection(Constant.ANALYTICS).document(Constant.TASK).get().get().getData();
        } catch (Exception exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            Constant.eventBus.post(new LogErrorEvent().setMessage("FireBaseService :: getTaskStatus :: Failed get Task Status.").setException(exception));
        } finally {
            if (remoteTaskListMap == null) {
                remoteTaskListMap = Collections.emptyMap();
            }
        }
        return remoteTaskListMap;
    }
}
