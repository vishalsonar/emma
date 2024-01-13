package com.sonar.vishal.emma.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.sonar.vishal.emma.entity.EmmaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmmaFireBaseService implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(EmmaFireBaseService.class);
    private static final String SERVICE_ACCOUNT_FILE_NAME = "emma-service-account.json";
    private static final String DOCUMENT_DATE_FORMAT_PATTERN = "dd-MM-yyyy";
    private static final String ANALYTICS = "ANALYTICS";
    private static final String FREQUENCY = "FREQUENCY";

    private static Firestore firestore;

    private static Map<String, Map<String, EmmaData>> collectionMap;

    static {
        collectionMap = new HashMap<>();
        try {
            if (firestore == null) {
                InputStream serviceAccount = EmmaFireBaseService.class.getClassLoader().getResourceAsStream(SERVICE_ACCOUNT_FILE_NAME);
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).build();
                FirebaseApp.initializeApp(options);
                firestore = FirestoreClient.getFirestore();
            }
        } catch (Exception e) {
            LOG.error("EmmaFireBaseService :: static :: Error while initializing account.", e);
        }
    }

    public void addOrUpdateDocument(List<EmmaData> emmaDataList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DOCUMENT_DATE_FORMAT_PATTERN);
        String documentName = dateFormat.format(new Date());
        emmaDataList.stream().forEach(data -> firestore.collection(documentName).document(data.getCompanyName()).set(data));
        Map<String, EmmaData> emmaDataMap = new HashMap<>();
        emmaDataList.stream().forEach(data -> emmaDataMap.put(data.getCompanyName(), data));
        collectionMap.put(documentName, emmaDataMap);
        updateFrequency(emmaDataList);
    }

    public Map<String, EmmaData> getCollectionMapData(String collectionName) {
        refreshCache(collectionName);
        Map<String, EmmaData> emmaDataMap = new HashMap<>();
        emmaDataMap.putAll(collectionMap.get(collectionName));
        return emmaDataMap;
    }

    public Map<String, Object> getFrequencyData() {
        Map<String, Object> frequencyData = new HashMap<>();
        try {
            Map<String, Object> tempFrequencyData = firestore.collection(ANALYTICS).document(FREQUENCY).get().get().getData();
            if (tempFrequencyData != null && !tempFrequencyData.isEmpty()) {
                frequencyData.putAll(tempFrequencyData);
            }
        } catch (InterruptedException interruptedException) {
            LOG.error("EmmaFireBaseService :: getFrequencyData :: Thread Interrupted Exception.", interruptedException);
            Thread.currentThread().interrupt();
        } catch (Exception exception) {
            LOG.error("EmmaFireBaseService :: getFrequencyData :: Failed to update Frequency.", exception);
        }
        return frequencyData;
    }

    private void updateFrequency(List<EmmaData> emmaDataList) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DOCUMENT_DATE_FORMAT_PATTERN);
            String documentName = dateFormat.format(new Date());
            Map<String, Long> frequencyMap = new HashMap<>();
            frequencyMap.putAll(emmaDataList.stream().collect(Collectors.groupingBy(EmmaData::getCompanyName, Collectors.counting())));
            firestore.collection(ANALYTICS).document(documentName).set(frequencyMap);
            List<QueryDocumentSnapshot> dataList = firestore.collection(ANALYTICS).get().get().getDocuments().stream()
                    .filter(document -> !document.getId().equals(documentName) && !document.getId().equals(FREQUENCY)).toList();

            if (!dataList.isEmpty()) {
                Map<String, Object> frequencyData = new HashMap<>();
                frequencyData.putAll(firestore.collection(ANALYTICS).document(FREQUENCY).get().get().getData());
                dataList.stream().forEach(document -> document.getData().entrySet().stream().forEach(entry -> {
                        if (frequencyData.containsKey(entry.getKey())) {
                            frequencyData.put(entry.getKey(), Long.valueOf(String.valueOf(frequencyData.get(entry.getKey()))) + 1);
                        }
                    }));
                firestore.collection(ANALYTICS).document(FREQUENCY).set(frequencyData);
            }
        } catch (InterruptedException interruptedException) {
            LOG.error("EmmaFireBaseService :: updateFrequency :: Thread Interrupted Exception.", interruptedException);
            Thread.currentThread().interrupt();
        } catch (Exception exception) {
            LOG.error("EmmaFireBaseService :: updateFrequency :: Failed to update Frequency.", exception);
        }
    }

    private void refreshCache(String collectionName) {
        try {
            if (!collectionMap.containsKey(collectionName)) {
                Map<String, EmmaData> emmaDataMap = new HashMap<>();
                firestore.collection(collectionName).get().get().getDocuments().forEach(document -> emmaDataMap.put(document.getId(), document.toObject(EmmaData.class)));
                collectionMap.put(collectionName, emmaDataMap);
            }
        } catch (InterruptedException interruptedException) {
            LOG.error("EmmaFireBaseService :: refreshCache :: Thread Interrupted Exception.", interruptedException);
            Thread.currentThread().interrupt();
        } catch (Exception exception) {
            LOG.error("EmmaFireBaseService :: refreshCache :: Failed to update cache.", exception);
        }
    }
}
