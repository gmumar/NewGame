package Purchases;

import wrapper.Globals;
import UserPackage.ItemsLookupPrefix;
import UserPackage.User;

public class GamePurchaseObserver {

	public void handleInstall() {
		System.out.println("GamePurchaseObserver: installed");
	}

	public void handleInstallError(Throwable arg0) {
		System.out.println("GamePurchaseObserver: install failed");
	}

	public void handlePurchaseFinished(GamePurchaseResult gamePurchaseResult) {
		System.out.println("GamePurchaseObserver: handle purchase");

		if (gamePurchaseResult == null) {
			return;
		}

		if (gamePurchaseResult.getSku() == null
				|| gamePurchaseResult.getMessage() == null) {
			return;
		}

		if (gamePurchaseResult.getResponse() == GamePurchaseResult.ANDROID_BILLING_RESPONSE_RESULT_OK) {
			if (gamePurchaseResult.getSku().compareTo(IAPManager.PACK_ONE) == 0) {
				User.getInstance().addCoin(10000);
			}
			if (gamePurchaseResult.getSku().compareTo(IAPManager.PACK_TWO) == 0) {
				User.getInstance().addCoin(100000);
				User.getInstance().buyItem(ItemsLookupPrefix.NO_ADS, 0);
			}
			if (gamePurchaseResult.getSku().compareTo(IAPManager.PACK_THREE) == 0) {
				User.getInstance().addCoin(1000000);
				User.getInstance().buyItem(ItemsLookupPrefix.NO_ADS, 0);
			}
			if (gamePurchaseResult.getSku().compareTo(IAPManager.PACK_FOUR) == 0) {
				User.getInstance().addCoin(100000000);
				User.getInstance().buyItem(ItemsLookupPrefix.NO_ADS, 0);
			}

			if (gamePurchaseResult.getSku().compareTo(IAPManager.IAP_TEST) == 0) {
				if(Globals.ADMIN_MODE){
					User.getInstance().addCoin(100000000);
				}
				//User.getInstance().buyItem(ItemsLookupPrefix.NO_ADS, 0);
			}
		}
	}

	public void handleConsumeFinished(
			Purchases.GamePurchaseResult gamePurchaseResult) {
		System.out.println("GamePurchaseObserver: handle consume ");
	}

	public void handleRecievedInformation(
			Purchases.GamePurchaseResult gamePurchaseResult) {
		System.out.println("GamePurchaseObserver: handle information ");
	}

	/*
	 * public void handlePurchase(Transaction arg0) {
	 * System.out.println("GamePurchaseObserver: handling purchase");
	 * 
	 * }
	 * 
	 * public void handlePurchaseCanceled() {
	 * System.out.println("GamePurchaseObserver: purchase canclled");
	 * 
	 * }
	 * 
	 * public void handlePurchaseError(Throwable arg0) {
	 * System.out.println("GamePurchaseObserver: purchase error");
	 * //arg0.printStackTrace(); arg0.getCause().printStackTrace();
	 * 
	 * }
	 * 
	 * public void handleRestore(Transaction[] arg0) {
	 * System.out.println("GamePurchaseObserver: purchase restore");
	 * 
	 * for (Transaction item : arg0){
	 * System.out.println("GamePurchaseObserver: " + item.getIdentifier() + " "
	 * + item.isPurchased()); }
	 * 
	 * }
	 * 
	 * public void handleRestoreError(Throwable arg0) {
	 * System.out.println("GamePurchaseObserver: retore error");
	 * 
	 * }
	 */

}
