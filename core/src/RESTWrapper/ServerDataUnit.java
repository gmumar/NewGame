package RESTWrapper;

public class ServerDataUnit {

	private String data;
	private String objectId;
	private float trackBestTime;

	public float getTrackBestTime() {
		return trackBestTime;
	}

	public void setTrackBestTime(float bestTime) {
		this.trackBestTime = bestTime;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

}
