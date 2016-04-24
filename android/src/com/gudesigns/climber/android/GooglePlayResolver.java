package com.gudesigns.climber.android;

import Purchases.PlatformResolver;

import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.gudesigns.climber.GameLoader;

public class GooglePlayResolver extends PlatformResolver {

    private final static String GOOGLEKEY  = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhXVntip+1UisIf9ahWWVybM5B/6/imYU+UVCoCcDGv7AQ9QQT3SMsjqgu8MRQ6L5W8saEPBEPoZOxtBw55X4yG/aCogNjahAUwAzVzh2c5hzLnMf/fZ6HAFzo7Zpa4AJ2ht+LSR7QLMB/8ozUeJ1m1yY+MrVT3K0YPzRo6jEuiDzw4LNrnkGNaGYjQGFDfiIjVEM5hBFnHRRffdP0s44TWY5cadqfUvebIFjlZgOSHWx91Ot+22S786Q3FAyky4iRkVqEwk81gXBRv8jvgT8bIpQVZKk417FQM/VE+zxZFdvo1cNnsyOwli5nNHWslODrvQiCaNez/EFiK5nQmSqPwIDAQAB";

    static final int RC_REQUEST = 10001;	// (arbitrary) request code for the purchase flow

    public GooglePlayResolver(GameLoader game) {
        super(game);

        PurchaseManagerConfig config = game.purchaseManager.purchaseManagerConfig;
        config.addStoreParam(PurchaseManagerConfig.STORE_NAME_ANDROID_GOOGLE, GOOGLEKEY);
        initializeIAP(null, game.purchaseManager.purchaseObserver, config);
    }
}