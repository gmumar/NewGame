package Purchases;

import com.gudesigns.climber.GameLoader;

public abstract class IAPManager {

	public static final String IAP_TEST = "android.test.purchased";

	public static final String PACK_ONE = "pack_one";

	public GameLoader game;
	protected PurchaseManager mgr;
	private boolean initialized = false;
	GamePurchaseObserver purchaseObserver;
	// PurchaseManagerConfig config;

	boolean purchaseEnabled = true;

	protected abstract void purchase(String productString,
			GamePurchaseObserver gamePurchaseObserver);

	protected abstract void getInformation(
			GamePurchaseObserver gamePurchaseObserver);

	protected abstract void purchaseRestore(final GamePurchaseObserver listener);

	protected void installFailed() { }

	protected void installIAP(GameLoader game) {
		this.game = game;
		
		initialized = true;
		purchaseObserver = new GamePurchaseObserver();

		purchaseRestore(purchaseObserver);
		

	}

	public void requestPurchase(String productString) {

		if (initialized) {
			purchase(productString, purchaseObserver);
		} else {
			System.out
					.println("ERROR: requestPurchase(): purchaseManager == null");
		}
	}

	public void requestInformation(GamePurchaseObserver gamePurchaseObserver) {
		
		getInformation(gamePurchaseObserver);

	}

	public void requestPurchaseRestore() {
		if (initialized) {
			purchaseRestore(purchaseObserver); 
		} else {
			System.out
					.println("ERROR: requestPurchaseRestore(): purchaseManager == null");
		}
	}

	public PurchaseManager getPurchaseManager() {
		return mgr;
	}

	public void dispose() {
		if (mgr != null) {
			mgr = null;
		}
	}

}