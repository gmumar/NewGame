package com.gudesigns.climber.android;

import util.IabHelper;
import util.IabHelper.IabAsyncInProgressException;
import util.IabResult;
import util.Inventory;
import util.Purchase;
import Purchases.GamePurchaseObserver;
import Purchases.GamePurchaseResult;
import Purchases.PlatformResolver;
import android.content.Intent;
import android.util.Log;

public class GooglePurchaseManager extends PlatformResolver {

	private final static String GOOGLEKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhXVntip+1UisIf9ahWWVybM5B/6/imYU+UVCoCcDGv7AQ9QQT3SMsjqgu8MRQ6L5W8saEPBEPoZOxtBw55X4yG/aCogNjahAUwAzVzh2c5hzLnMf/fZ6HAFzo7Zpa4AJ2ht+LSR7QLMB/8ozUeJ1m1yY+MrVT3K0YPzRo6jEuiDzw4LNrnkGNaGYjQGFDfiIjVEM5hBFnHRRffdP0s44TWY5cadqfUvebIFjlZgOSHWx91Ot+22S786Q3FAyky4iRkVqEwk81gXBRv8jvgT8bIpQVZKk417FQM/VE+zxZFdvo1cNnsyOwli5nNHWslODrvQiCaNez/EFiK5nQmSqPwIDAQAB";
	static final int RC_REQUEST = 10001;
	private IabHelper mHelper;
	private AndroidLauncher mainApp;

	public GooglePurchaseManager(AndroidLauncher androidLauncher) {
		super();

		mainApp = androidLauncher;
		mHelper = new IabHelper(androidLauncher, GOOGLEKEY);

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					Log.d("IAB", "Problem setting up In-app Billing: " + result);
				}
				// Hooray, IAB is fully set up!
				Log.d("IAB", "Billing Success: " + result);

				if (result.isSuccess()) {
					installIAP();
				} else {
					installFailed();
				}

			}

		});
	}
	
	

	@Override
	public void purchase(String sku, final GamePurchaseObserver listener) {
		try {
			mHelper.launchPurchaseFlow(mainApp, sku, RC_REQUEST,
					new IabHelper.OnIabPurchaseFinishedListener() {

						@Override
						public void onIabPurchaseFinished(IabResult result,
								Purchase item) {
							Log.d("IAB", "IAB purchase finished " + result);
							listener.handlePurchaseFinished(GamePurchaseResult(
									result, item));

							if (result.isSuccess()) {
								consume(item, listener);
							}
						}
					});
		} catch (IabAsyncInProgressException e) {
			e.printStackTrace();
		}
	}

	private static final GamePurchaseResult GamePurchaseResult(
			IabResult result, Purchase info) {
		GamePurchaseResult ret = new GamePurchaseResult();

		ret.setMessage(result.getMessage());
		ret.setResponse(result.getResponse());

		return ret;

	}

	@Override
	public void purchaseRestore(final GamePurchaseObserver listener) {

		try {
			Inventory items = mHelper.queryInventory();

			if (items.hasPurchase(PlatformResolver.PACK_ONE)) {
				consume(items.getPurchase(PACK_ONE), listener);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void handleActivityResult(int requestCode, int resultCode, Intent data){
		mHelper.handleActivityResult(requestCode, resultCode, data);
	}

	private void consume(Purchase item, final GamePurchaseObserver listener) {
		try {
			mHelper.consumeAsync(item,
					new IabHelper.OnConsumeFinishedListener() {

						@Override
						public void onConsumeFinished(Purchase purchase,
								IabResult result) {

							listener.handleConsumeFinished(GamePurchaseResult(
									result, purchase));

						}
					});
		} catch (IabAsyncInProgressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
