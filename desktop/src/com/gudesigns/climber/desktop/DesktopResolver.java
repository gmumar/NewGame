package com.gudesigns.climber.desktop;


import Purchases.GamePurchaseObserver;
import Purchases.IAPManager;

import com.gudesigns.climber.GameLoader;

public class DesktopResolver extends IAPManager {

    public DesktopResolver(GameLoader game) {
        super(game);
    }

	@Override
	public void purchase(String productString,
			GamePurchaseObserver gamePurchaseObserver) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void purchaseRestore(GamePurchaseObserver listener) {
		// TODO Auto-generated method stub
		
	}
}