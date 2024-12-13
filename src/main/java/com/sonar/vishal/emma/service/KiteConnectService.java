package com.sonar.vishal.emma.service;

import com.sonar.vishal.emma.bus.LogErrorEvent;
import com.sonar.vishal.emma.listener.TradeOnTickerArrivalListener;
import com.sonar.vishal.emma.util.Constant;
import com.sonar.vishal.emma.util.TradeAlgorithmMap;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.User;
import com.zerodhatech.ticker.KiteTicker;
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

    @Value("${application.kite.connect.kite.ticker.api.try.reconnection}")
    private Boolean tryReconnection;

    @Value("${application.kite.connect.kite.ticker.api.maximum.retries}")
    private Integer maximumRetries;

    @Value("${application.kite.connect.kite.ticker.api.maximum.retry.interval}")
    private Integer maximumRetryInterval;

    private transient KiteTicker kiteTicker;
    private transient KiteConnect kiteConnect;

    public void login() {
        try {
            kiteConnect = new KiteConnect(apiKey);
            final User userModel = kiteConnect.generateSession(apiRequestToken, apiSecret);
            kiteConnect.setUserId(userId);
            kiteConnect.setAccessToken(userModel.accessToken);
            kiteConnect.setPublicToken(userModel.publicToken);
            kiteConnect.setSessionExpiryHook(this::logout);

            kiteTicker = new KiteTicker(userModel.accessToken, apiKey);
            kiteTicker.setTryReconnection(tryReconnection);
            kiteTicker.setMaximumRetries(maximumRetries);
            kiteTicker.setMaximumRetryInterval(maximumRetryInterval);
            kiteTicker.connect();

            kiteConnect.getInstruments(Constants.EXCHANGE_NSE).stream().forEach(instrument -> TradeAlgorithmMap.NSE_TRADE_TOKEN.put(instrument.getTradingsymbol(), instrument.getInstrument_token()));
            kiteConnect.getInstruments(Constants.EXCHANGE_BSE).stream().forEach(instrument -> TradeAlgorithmMap.BSE_TRADE_TOKEN.put(instrument.getTradingsymbol(), instrument.getInstrument_token()));
            kiteTicker.setOnTickerArrivalListener(new TradeOnTickerArrivalListener());
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
            kiteTicker.disconnect();
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

    public KiteTicker getKiteTicker() {
        return kiteTicker;
    }
}
