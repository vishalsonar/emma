package com.sonar.vishal.emma.service;

import com.sonar.vishal.emma.bus.LogErrorEvent;
import com.sonar.vishal.emma.context.Context;
import com.sonar.vishal.emma.listener.TradeOnTickerArrivalListener;
import com.sonar.vishal.emma.util.Constant;
import com.sonar.vishal.emma.util.TradeAlgorithmMap;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.User;
import com.zerodhatech.ticker.KiteTicker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Profile("KITE")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
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

    @Autowired
    private transient TradeOnTickerArrivalListener tradeOnTickerArrivalListener;

    private transient KiteTicker kiteTicker;
    private transient KiteConnect kiteConnect;

    public synchronized void login() {
        try {
            kiteConnect = Context.getBean(KiteConnect.class, apiKey);
            final User userModel = kiteConnect.generateSession(apiRequestToken, apiSecret);
            kiteConnect.setUserId(userId);
            kiteConnect.setAccessToken(userModel.accessToken);
            kiteConnect.setPublicToken(userModel.publicToken);
            kiteConnect.setSessionExpiryHook(this::logout);

            kiteTicker = Context.getBean(KiteTicker.class, userModel.accessToken, apiKey);
            kiteTicker.setTryReconnection(tryReconnection);
            kiteTicker.setMaximumRetries(maximumRetries);
            kiteTicker.setMaximumRetryInterval(maximumRetryInterval);
            kiteTicker.connect();

            kiteConnect.getInstruments(Constants.EXCHANGE_NSE).stream().forEach(instrument -> TradeAlgorithmMap.NSE_TRADE_TOKEN.put(instrument.getTradingsymbol(), instrument.getInstrument_token()));
            kiteConnect.getInstruments(Constants.EXCHANGE_BSE).stream().forEach(instrument -> TradeAlgorithmMap.BSE_TRADE_TOKEN.put(instrument.getTradingsymbol(), instrument.getInstrument_token()));
            kiteTicker.setOnTickerArrivalListener(tradeOnTickerArrivalListener);
        } catch (KiteException kiteException) {
            kiteTicker = null;
            kiteConnect = null;
            Constant.LOG_EVENT_BUS.post(Context.getBean(LogErrorEvent.class).setMessage("KiteConnectService :: login :: Error while initializing KiteConnect. :: " + kiteException.message).setException(kiteException));
        } catch (Exception exception) {
            kiteTicker = null;
            kiteConnect = null;
            Constant.LOG_EVENT_BUS.post(Context.getBean(LogErrorEvent.class).setMessage("KiteConnectService :: login :: Error while initializing KiteConnect.").setException(exception));
        }
    }

    public synchronized void logout() {
        try {
            if (kiteTicker != null) {
                kiteTicker.disconnect();
            }
            if (kiteConnect != null) {
                kiteConnect.logout();
            }
        } catch (KiteException kiteException) {
            Constant.LOG_EVENT_BUS.post(Context.getBean(LogErrorEvent.class).setMessage("KiteConnectService :: logout :: " + kiteException.message).setException(kiteException));
        } catch (Exception exception) {
            Constant.LOG_EVENT_BUS.post(Context.getBean(LogErrorEvent.class).setMessage("KiteConnectService :: logout :: Error in logout.").setException(exception));
        } finally {
            kiteTicker = null;
            kiteConnect = null;
        }
    }

    public synchronized void updateOrder() {
        try {
            kiteConnect.getOrders().stream().forEach(order -> {
                Map<String, Order> orderMap = TradeAlgorithmMap.TRADE_ORDER.get(order.tradingSymbol);
                if (orderMap == null) {
                    orderMap = Context.getBean(HashMap.class);
                }
                orderMap.put(order.orderId, order);
                TradeAlgorithmMap.TRADE_ORDER.put(order.tradingSymbol, orderMap);
            });
        } catch (KiteException kiteException) {
            Constant.LOG_EVENT_BUS.post(Context.getBean(LogErrorEvent.class).setMessage("KiteConnectService :: updateOrder :: " + kiteException.message).setException(kiteException));
        } catch (Exception exception) {
            Constant.LOG_EVENT_BUS.post(Context.getBean(LogErrorEvent.class).setMessage("KiteConnectService :: updateOrder :: Error in updating order.").setException(exception));
        }
    }

    public synchronized KiteConnect getKiteConnect() {
        return kiteConnect;
    }

    public synchronized KiteTicker getKiteTicker() {
        return kiteTicker;
    }

    public synchronized boolean isConnectionOpen() {
        return kiteConnect != null && kiteTicker != null && kiteTicker.isConnectionOpen();
    }

    public synchronized void subscribe(ArrayList<Long> tokens) {
        kiteTicker.subscribe(tokens);
        kiteTicker.setMode(tokens, KiteTicker.modeLTP);
    }
}
