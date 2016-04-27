package Purchases;

public class GamePurchaseResult {

	public static final int ANDROID_BILLING_RESPONSE_RESULT_OK = 0;

	int mResponse;
	String mMessage;

	public void setResponse(int mResponse) {
		this.mResponse = mResponse;
	}

	public void setMessage(String mMessage) {
		this.mMessage = mMessage;
	}

	public int getResponse() {
		return mResponse;
	}

	public String getMessage() {
		return mMessage;
	}

	public boolean isSuccess() {
		return mResponse == ANDROID_BILLING_RESPONSE_RESULT_OK;
	}

	public boolean isFailure() {
		return !isSuccess();
	}

	public String toString() {
		return "IabResult: " + getMessage();
	}

}
