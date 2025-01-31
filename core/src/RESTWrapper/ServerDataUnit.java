package RESTWrapper;

public class ServerDataUnit {

	private String data;
	private String objectId;
	private float trackBestTime;
	private int trackDifficulty;
	private int itemIndex;
	private String createdAt;
	private String sourceUser;
	private String targetUser;
	private Integer challengeReward;
	

	public Integer getChallengeReward() {
		return challengeReward;
	}

	public void setChallengeReward(Integer challengeReward) {
		this.challengeReward = challengeReward;
	}

	public String getSourceUser() {
		return sourceUser;
	}

	public void setSourceUser(String sourceUser) {
		this.sourceUser = sourceUser;
	}

	public String getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(String targetUser) {
		this.targetUser = targetUser;
	}

	public int getItemIndex() {
		return itemIndex;
	}

	public void setItemIndex(int trackIndex) {
		this.itemIndex = trackIndex;
	}

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

	public void setCreationTime(String asString) {
		createdAt = asString;
	}

	public String getCreationTime() {
		return createdAt;
	}

}
