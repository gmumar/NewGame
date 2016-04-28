package Purchases;

public class PurchaseManager {
	
	// link doc : https://bitbucket.org/just4phil/gdxpayexample/
	
	// ----- app stores -------------------------
	public static final int APPSTORE_UNDEFINED	= 0;
	public static final int APPSTORE_GOOGLE 	= 1;
	public static final int APPSTORE_OUYA 		= 2;
	public static final int APPSTORE_AMAZON 	= 3;
	public static final int APPSTORE_DESKTOP 	= 4;

	private int isAppStore = APPSTORE_UNDEFINED;

	static IAPManager m_platformResolver;

	public IAPManager getPlatformResolver() {
		return m_platformResolver;
	}
	public static void setPlatformResolver (IAPManager platformResolver) {
		m_platformResolver = platformResolver;
	}

	public int getAppStore () {
		return isAppStore;
	}
	public void setAppStore (int isAppStore) {
		this.isAppStore = isAppStore;
	}

}
