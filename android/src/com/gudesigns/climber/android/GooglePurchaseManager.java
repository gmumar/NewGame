package com.gudesigns.climber.android;

import java.util.ArrayList;
import java.util.List;

import util.IabHelper;
import util.IabHelper.IabAsyncInProgressException;
import util.IabResult;
import util.Inventory;
import util.Purchase;
import util.SkuDetails;
import Purchases.GameItemInformation;
import Purchases.GamePurchaseObserver;
import Purchases.GamePurchaseResult;
import Purchases.IAPManager;
import android.content.Intent;
import android.util.Log;

import com.gudesigns.climber.GameLoader;

public class GooglePurchaseManager extends IAPManager {

	private final static String GOOGLEKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhXVntip+1UisIf9ahWWVybM5B/6/imYU+UVCoCcDGv7AQ9QQT3SMsjqgu8MRQ6L5W8saEPBEPoZOxtBw55X4yG/aCogNjahAUwAzVzh2c5hzLnMf/fZ6HAFzo7Zpa4AJ2ht+LSR7QLMB/8ozUeJ1m1yY+MrVT3K0YPzRo6jEuiDzw4LNrnkGNaGYjQGFDfiIjVEM5hBFnHRRffdP0s44TWY5cadqfUvebIFjlZgOSHWx91Ot+22S786Q3FAyky4iRkVqEwk81gXBRv8jvgT8bIpQVZKk417FQM/VE+zxZFdvo1cNnsyOwli5nNHWslODrvQiCaNez/EFiK5nQmSqPwIDAQAB";
	static final int RC_REQUEST = 10001;
	private IabHelper mHelper;
	private AndroidLauncher mainApp;

	public GooglePurchaseManager(AndroidLauncher androidLauncher,
			final GameLoader game) {
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
					installIAP(game);
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

	@Override
	public void purchaseRestore(final GamePurchaseObserver listener) {

		try {
			Inventory items = mHelper.queryInventory();

			if (items.hasPurchase(IAP_TEST)) {
				consume(items.getPurchase(IAP_TEST), listener);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void getInformation(final GamePurchaseObserver listener) {
		System.out.println("GooglePurchase: getting information");

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {

					List<String> moreItemSkus = new ArrayList<String>();

					moreItemSkus.add(IAPManager.PACK_ONE);

					Inventory inv = mHelper.queryInventory(true, moreItemSkus,
							null);

					game.IAPItemInformation.put(PACK_ONE,
							GameItemInformation(inv.getSkuDetails(PACK_ONE)));

					listener.handleRecievedInformation(GamePurchaseResult(null,
							inv));

					System.out.println("GooglePurchase: sent request");
				} catch (Exception e) {
					System.out
							.println("GooglePurchase: getting information failed");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		t.start();

	}

	public void handleActivityResult(int requestCode, int resultCode,
			Intent data) {
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

	private static final GamePurchaseResult GamePurchaseResult(
			IabResult result, Purchase info) {
		GamePurchaseResult ret = new GamePurchaseResult();

		if (result != null) {
			ret.setMessage(result.getMessage());
			ret.setResponse(result.getResponse());
		}

		if (info != null) {
			ret.setItemSku(info.getSku());
		}

		return ret;

	}

	private static final GamePurchaseResult GamePurchaseResult(
			IabResult result, Inventory info) {
		GamePurchaseResult ret = new GamePurchaseResult();

		// ret.setMessage(result.getMessage());
		// ret.setResponse(result.getResponse());

		return ret;

	}

	private static final GameItemInformation GameItemInformation(
			SkuDetails details) {
		GameItemInformation ret = new GameItemInformation();

		ret.setPrice(details.getPrice());

		return ret;

	}

}
