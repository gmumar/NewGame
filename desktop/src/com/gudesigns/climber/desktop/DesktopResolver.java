package com.gudesigns.climber.desktop;


import Purchases.GamePurchaseObserver;
import Purchases.PlatformResolver;

import com.gudesigns.climber.GameLoader;

public class DesktopResolver extends PlatformResolver {

    public DesktopResolver(GameLoader game) {
        super(game);
    }

	@Override
	public void purchase(String productString,
			GamePurchaseObserver gamePurchaseObserver) {
		// TODO Auto-generated method stub
		
	}
}