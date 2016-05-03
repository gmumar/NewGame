package RESTWrapper;

public class ServerDataUnit {

	private String data;
	private String objectId;
	private float trackBestTime;
	private int trackDifficulty;

	public int getTrackDifficulty() {
		return trackDifficulty;
	}

	public void setTrackDifficulty(int trackDifficulty) {
		this.trackDifficulty = trackDifficulty;
	}

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
