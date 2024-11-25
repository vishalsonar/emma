package com.sonar.vishal.emma.service;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Margin;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;
import com.zerodhatech.models.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;

@Service
public class KiteConnectService implements Serializable {

    private KiteConnect kiteConnect = getKiteConnect();

    private KiteConnect getKiteConnect() {
        if (kiteConnect == null) {
            kiteConnect = new KiteConnect("your_apiKey");
            kiteConnect.setUserId("your_userId");
            String url = kiteConnect.getLoginURL();
        }
        return kiteConnect;
    }


    public void test() throws IOException, KiteException {
        // Initialize Kiteconnect using apiKey.
        KiteConnect kiteSdk = new KiteConnect("your_apiKey");

        // Set userId.
        kiteSdk.setUserId("your_userId");

        /* First you should get request_token, public_token using kitconnect login and then use request_token, public_token, api_secret to make any kiteconnect api call.
        Get login url. Use this url in webview to login user, after authenticating user you will get requestToken. Use the same to get accessToken. */
        String url = kiteSdk.getLoginURL();

        // Get accessToken as follows,
        User userModel = kiteSdk.generateSession("request_token", "your_apiSecret");

        // Set request token and public token which are obtained from login process.
        kiteSdk.setAccessToken(userModel.accessToken);
        kiteSdk.setPublicToken(userModel.publicToken);

        // Set session expiry callback.
        kiteSdk.setSessionExpiryHook(new SessionExpiryHook() {
            @Override
            public void sessionExpired() {
                System.out.println("session expired");
            }
        });

        // Get margins returns margin model, you can pass equity or commodity as arguments to get margins of respective segments.
        Margin margins = kiteSdk.getMargins("equity");
        System.out.println(margins.available.cash);
        System.out.println(margins.utilised.debits);


        /** Place order method requires a orderParams argument which contains,
         * tradingsymbol, exchange, transaction_type, order_type, quantity, product, price, trigger_price, disclosed_quantity, validity
         * squareoff_value, stoploss_value, trailing_stoploss
         * and variety (value can be regular, bo, co, amo)
         * place order will return order model which will have only orderId in the order model
         * Following is an example param for LIMIT order,
         * if a call fails then KiteException will have error message in it
         * Success of this call implies only order has been placed successfully, not order execution. */

        OrderParams orderParams = new OrderParams();
        orderParams.quantity = 1;
        orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
        orderParams.tradingsymbol = "ASHOKLEY";
        orderParams.product = Constants.PRODUCT_CNC;
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.price = 122.2;
        orderParams.triggerPrice = 0.0;
        orderParams.tag = "myTag"; //tag is optional and it cannot be more than 8 characters and only alphanumeric is allowed

        Order order = kiteSdk.placeOrder(orderParams, Constants.VARIETY_REGULAR);
        System.out.println(order.orderId);
    }
}
