package com.sonar.vishal.emma.service;

import com.sonar.vishal.emma.bus.LogErrorEvent;
import com.sonar.vishal.emma.util.Constant;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
@Profile("KITE")
public class KiteConnectService implements Serializable {

    @Value("${application.kite.connect.api.key}")
    private String apiKey;

    @Value("${application.kite.connect.api.secret}")
    private String apiSecret;

    @Value("${application.kite.connect.api.request.token}")
    private String apiRequestToken;

    @Value("${application.kite.connect.api.user.id}")
    private String userId;

    private transient KiteConnect kiteConnect;

    public void login() {
        try {
            kiteConnect = new KiteConnect(apiKey);
            final User userModel = kiteConnect.generateSession(apiRequestToken, apiSecret);
            kiteConnect.setUserId(userId);
            kiteConnect.setAccessToken(userModel.accessToken);
            kiteConnect.setPublicToken(userModel.publicToken);
            kiteConnect.setSessionExpiryHook(this::logout);
        } catch (KiteException kiteException) {
            kiteConnect = null;
            Constant.LOG_EVENT_BUS.post(new LogErrorEvent().setMessage("KiteConnectService :: login :: Error while initializing KiteConnect. :: " + kiteException.message).setException(kiteException));
        } catch (Exception exception) {
            kiteConnect = null;
            Constant.LOG_EVENT_BUS.post(new LogErrorEvent().setMessage("KiteConnectService :: login :: Error while initializing KiteConnect.").setException(exception));
        }
    }

    public void logout() {
        try {
            kiteConnect.logout();
        } catch (KiteException kiteException) {
            Constant.LOG_EVENT_BUS.post(new LogErrorEvent().setMessage("KiteConnectService :: logout :: " + kiteException.message).setException(kiteException));
        } catch (Exception exception) {
            Constant.LOG_EVENT_BUS.post(new LogErrorEvent().setMessage("KiteConnectService :: logout :: Error in logout.").setException(exception));
        } finally {
            kiteConnect = null;
        }
    }

    public KiteConnect getKiteConnect() {
        return kiteConnect;
    }
}
