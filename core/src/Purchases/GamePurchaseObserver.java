package Purchases;

import com.badlogic.gdx.pay.PurchaseObserver;
import com.badlogic.gdx.pay.Transaction;


public class GamePurchaseObserver  implements PurchaseObserver{

	@Override
	public void handleInstall() {
		
		System.out.print("GamePurchaseObserver: installed");
	}

	@Override
	public void handleInstallError(Throwable arg0) {
		System.out.print("GamePurchaseObserver: install failed");
		
	}

	@Override
	public void handlePurchase(Transaction arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handlePurchaseCanceled() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handlePurchaseError(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleRestore(Transaction[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleRestoreError(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}

}
