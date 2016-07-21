package JSONifier;

import com.google.gson.Gson;

public class JSONChallenge {

	private String challenge;
	private String sourceUser;
	private String targetUser;
	private float bestTime;
	private Integer reward;
	
	public Integer getReward() {
		return reward;
	}

	public void setReward(Integer reward) {
		this.reward = reward;
	}

	public String jsonify() {
		Gson obj = new Gson();
		//obj.setIgnoreUnknownFields(true);
		return obj.toJson(this);
	}

	public static JSONChallenge objectify(String data) {
		Gson json = new Gson();
		//json.setIgnoreUnknownFields(true);
		return json.fromJson(data, JSONChallenge.class);
	}

	public String getChallenge() {
		return challenge;
	}
	public void setChallenge(String challenge) {
		this.challenge = challenge;
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
	public float getBestTime() {
		return bestTime;
	}
	public void setBestTime(float bestTime) {
		this.bestTime = bestTime;
	}
	
	
}
