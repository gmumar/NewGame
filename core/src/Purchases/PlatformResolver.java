package Purchases;

import com.gudesigns.climber.GameLoader;

public abstract class PlatformResolver {

	public static final String IAP_TEST_CONSUMEABLE = "com.badlogic.gdx.tests.pay.consumeable";

	public static final String PACK_ONE = "android.test.purchased";

	public GameLoader game;
	protected PurchaseManager mgr;
	private boolean initialized = false;
	// PurchaseObserver purchaseObserver;
	// PurchaseManagerConfig config;

	boolean purchaseEnabled = true;

	public PlatformResolver(GameLoader game) {
		this.game = game;
	}

	public PlatformResolver() {
	}

	protected abstract void purchase(String productString,
			GamePurchaseObserver gamePurchaseObserver);

	protected abstract void purchaseRestore(final GamePurchaseObserver listener);

	public void initializeIAP(PurchaseManager mgr,
			GamePurchaseObserver purchaseObserver/*
												 * , PurchaseManagerConfig
												 * config
												 */) {
		this.mgr = mgr;
		// this.purchaseObserver = purchaseObserver;
		// this.config = config;
	}

	protected void installFailed() {
		// TODO Auto-generated method stub

	}

	protected void installIAP() {
		initialized = true;

		purchaseRestore(new GamePurchaseObserver());

		/*
		 * config.addOffer(new Offer() .setType(OfferType.CONSUMABLE)
		 * .setIdentifier(IAP_TEST_CONSUMEABLE) .putIdentifierForStore(
		 * PurchaseManagerConfig.STORE_NAME_ANDROID_GOOGLE,
		 * "android.test.purchased"));
		 * 
		 * //System.out.println("PlatformResolver: " +
		 * config.getOffer("android.test.purchased").getIdentifier());
		 * 
		 * // set and install the manager manually if (mgr != null) {
		 * PurchaseSystem.setManager(mgr);
		 * 
		 * config.addOffer(new Offer() .setType(OfferType.CONSUMABLE)
		 * .setIdentifier(IAP_TEST_CONSUMEABLE));
		 * 
		 * mgr.install(purchaseObserver, config, false); // dont call //
		 * PurchaseSystem.install() // because it may // bind openIAB!
		 * System.out.println("calls purchasemanager.install() manually"); }
		 * else {
		 * 
		 * Gdx.app.log("gdx-pay",
		 * "initializeIAP(): purchaseManager == null => call PurchaseSystem.hasManager()"
		 * ); if (PurchaseSystem.hasManager()) { // install and get the manager
		 * // automatically via reflection this.mgr =
		 * PurchaseSystem.getManager(); Gdx.app.log("gdx-pay",
		 * "calls PurchaseSystem.install() via reflection");
		 * PurchaseSystem.install(purchaseObserver, config); // install the //
		 * observer Gdx.app.log("gdx-pay", "installed manager: " +
		 * this.mgr.toString()); } }
		 */
	}

	public void requestPurchase(String productString) {

		if (initialized) {
			purchase(productString, new GamePurchaseObserver());
			// dont call PurchaseSystem... because
			// it may bind openIAB!
			System.out.println("calls purchasemanager.purchase()");
		} else {
			System.out.println(
					"ERROR: requestPurchase(): purchaseManager == null");
		}
	}

	/*
	 * public Information requestInformation(String productString) { if (mgr ==
	 * null) return new Information("NaN", "NaN", "0"); return
	 * mgr.getInformation(productString);
	 * 
	 * }
	 */

	public void requestPurchaseRestore() {
		if (initialized) {
			purchaseRestore(new GamePurchaseObserver()); // dont call
									// PurchaseSystem.purchaseRestore(); because
									// it may bind openIAB!
			System.out.println("calls purchasemanager.purchaseRestore()");
		} else {
			System.out.println(
					"ERROR: requestPurchaseRestore(): purchaseManager == null");
		}
	}

	public PurchaseManager getPurchaseManager() {
		return mgr;
	}

	public void dispose() {
		if (mgr != null) {
			System.out.println("calls purchasemanager.dispose()");
			// mgr.dispose(); // dont call PurchaseSystem... because it may bind
			// openIAB!
			mgr = null;
		}
	}

}