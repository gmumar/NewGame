package Purchases;

import com.badlogic.gdx.pay.Information;
import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.badlogic.gdx.pay.PurchaseSystem;

public class PurchaseManager {
	
	public PurchaseManager(){
		PurchaseSystem.onAppRestarted();
		
		if (PurchaseSystem.hasManager()) {

			  // purchase system is ready to start. Let's initialize our product list etc...
			  PurchaseManagerConfig config = new PurchaseManagerConfig();
			  //config.addOffer(...)
			  //config.addOffer(...)
			  
			  config.addStoreParam(PurchaseManagerConfig.STORE_NAME_ANDROID_GOOGLE, "<Google key>");
			  
			  // let's start the purchase system...
			  PurchaseSystem.install(new GamePurchaseObserver(), config);

			  // to make a purchase (results are reported to the observer)
			  PurchaseSystem.purchase("product_identifier"); 
			 
			  // (*) to restore existing purchases (results are reported to the observer)
			  //PurchaseSystem.restore();
			  
			  // obtain localized product information (not supported by all platforms)
			  Information information = PurchaseSystem.getInformation("product_identifier");
		}
	}

}
