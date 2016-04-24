package Purchases;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.pay.Offer;
import com.badlogic.gdx.pay.OfferType;
import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.badlogic.gdx.pay.PurchaseObserver;

public class PurchaseManager {
	
	// ----- app stores -------------------------
	public static final int APPSTORE_UNDEFINED	= 0;
	public static final int APPSTORE_GOOGLE 	= 1;
	public static final int APPSTORE_OUYA 		= 2;
	public static final int APPSTORE_AMAZON 	= 3;
	public static final int APPSTORE_DESKTOP 	= 4;

	private int isAppStore = APPSTORE_UNDEFINED;

	public final static String coin_pack_one = "pack_one";
	static PlatformResolver m_platformResolver;
	public PurchaseManagerConfig purchaseManagerConfig;
	public PurchaseObserver purchaseObserver = new GamePurchaseObserver();

	public PurchaseManager() {

		setAppStore(APPSTORE_GOOGLE);	// change this if you deploy to another platform

		// ---- IAP: define products ---------------------
		purchaseManagerConfig = new PurchaseManagerConfig();
		purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier(coin_pack_one));
		

	}
	
	protected boolean checkTransaction (String ID, boolean isRestore) {
		boolean returnbool = false;

		if (coin_pack_one.equals(ID)) {
			Gdx.app.log("checkTransaction", "full version found!");

			//----- put your logic for full version here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

			returnbool = true;
		}
		return returnbool;
	}

	public PlatformResolver getPlatformResolver() {
		return m_platformResolver;
	}
	public static void setPlatformResolver (PlatformResolver platformResolver) {
		m_platformResolver = platformResolver;
	}

	public int getAppStore () {
		return isAppStore;
	}
	public void setAppStore (int isAppStore) {
		this.isAppStore = isAppStore;
	}

}
