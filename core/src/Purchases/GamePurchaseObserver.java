package Purchases;

public class GamePurchaseObserver  {

	public void handleInstall() {
		System.out.println("GamePurchaseObserver: installed");
	}

	public void handleInstallError(Throwable arg0) {
		System.out.println("GamePurchaseObserver: install failed");
	}

	public void handlePurchaseFinished(GamePurchaseResult gamePurchaseResult) {
		System.out.println("GamePurchaseObserver: handle purchase");
	}

	public void handleConsumeFinished(
			Purchases.GamePurchaseResult gamePurchaseResult) {
		System.out.println("GamePurchaseObserver: handle consume ");
	}


	/*public void handlePurchase(Transaction arg0) {
		System.out.println("GamePurchaseObserver: handling purchase");
		
	}

	public void handlePurchaseCanceled() {
		System.out.println("GamePurchaseObserver: purchase canclled");
			
	}

	public void handlePurchaseError(Throwable arg0) {
		System.out.println("GamePurchaseObserver: purchase error");
		//arg0.printStackTrace();
		arg0.getCause().printStackTrace();
		
	}

	public void handleRestore(Transaction[] arg0) {
		System.out.println("GamePurchaseObserver: purchase restore");
		
		for (Transaction item : arg0){
			System.out.println("GamePurchaseObserver: " + item.getIdentifier() + " " + item.isPurchased());
		}
		
	}

	public void handleRestoreError(Throwable arg0) {
		System.out.println("GamePurchaseObserver: retore error");
		
	}*/

}
