package com.sonar.vishal.emma.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.sonar.vishal.emma.entity.EmmaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmmaFireBaseService {

    private static final Logger LOG = LoggerFactory.getLogger(EmmaFireBaseService.class);
    private static final String SERVICE_ACCOUNT_FILE_NAME = "emma-service-account.json";
    private static final String DOCUMENT_DATE_FORMAT_PATTERN = "dd-MM-yyyy";

    private static Map<String, Map<String, EmmaData>> collectionMap;

    static {
        collectionMap = new HashMap<>();
    }

    private Firestore firestore;

    public static Map<String, Map<String, EmmaData>> getCollectionMapData() {
        return collectionMap;
    }

    public EmmaFireBaseService() {
        try {
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream(SERVICE_ACCOUNT_FILE_NAME);
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).build();
            FirebaseApp.initializeApp(options);
            firestore = FirestoreClient.getFirestore();
            updateCollectionMap();
        } catch (Exception e) {
            LOG.error("EmmaFireBaseService :: constructor :: Error while initializing account.", e);
        }
    }

    public void addOrUpdateDocument(List<EmmaData> emmaDataList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DOCUMENT_DATE_FORMAT_PATTERN);
        emmaDataList.stream().forEach(data -> firestore.collection(dateFormat.format(new Date())).document(data.getCompanyName()).set(data));
        updateCollectionMap();
    }

    private void updateCollectionMap() {
        firestore.listCollections().forEach(collection -> {
            Map<String, EmmaData> emmaDataMap = new HashMap<>();
            collection.listDocuments().forEach(document -> {
                try {
                    emmaDataMap.put(document.getId(), document.get().get().toObject(EmmaData.class));
                } catch (InterruptedException interruptedException) {
                    LOG.error("EmmaScheduledTask :: execute :: Thread Interrupted Exception.", interruptedException);
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    LOG.error("EmmaFireBaseService :: updateCollectionMap :: Error while updating collection Map.", e);
                }
            });
            collectionMap.put(collection.getId(), emmaDataMap);
        });
    }
}
