package Multiplayer;

import java.util.ArrayList;

import JSONifier.JSONCar;
import JSONifier.JSONTrack.TrackType;
import RESTWrapper.BackendFunctions;
import RESTWrapper.RESTPaths;
import UserPackage.TrackMode;

import com.google.gson.Gson;

public class Challenge {

	private ArrayList<RecorderUnit> recording = new ArrayList<RecorderUnit>();
	private JSONCar carJson;

	private String trackObjectId;
	private String trackIndex;
	private String trackDifficulty;
	private TrackMode trackMode;
	private TrackType trackType;

	public Challenge(ArrayList<RecorderUnit> recording, JSONCar jsonCar,
			String trackObjectId, String trackIndex, String trackDifficulty,
			TrackMode trackMode, TrackType trackType) {
		super();
		this.recording = recording;
		this.carJson = jsonCar;
		this.trackObjectId = trackObjectId;
		this.trackMode = trackMode;
		this.trackType = trackType;
		this.trackIndex = trackIndex;
		this.trackDifficulty = trackDifficulty;
	}

	public static Challenge objectify(String data) {
		Gson json = new Gson();
		// json.setIgnoreUnknownFields(true);
		return json.fromJson(data, Challenge.class);
	}

	public static String createChallegeJSON(ArrayList<RecorderUnit> recording,
			JSONCar jsonCar, String trackObjectId, String trackIndex,
			String trackDifficulty, TrackMode trackMode, TrackType trackType) {
		Gson json = new Gson();

		Challenge c = new Challenge(recording, jsonCar, trackObjectId,
				trackIndex, trackDifficulty, trackMode, trackType);

		return json.toJson(c);
	}

	public static void submitChallenge(ArrayList<RecorderUnit> recording2,
			JSONCar car, String trackId, String trackIndex,
			String trackDifficulty, TrackMode currentTrackMode, TrackType type,
			String sourceUser, String targetUser, float mapTime, String reward) {

		BackendFunctions.uploadChallenge(
				RESTPaths.CHALLENGES,
				createChallegeJSON(recording2, car, trackId, trackIndex,
						trackDifficulty, currentTrackMode, type), sourceUser,
				targetUser, mapTime, reward);

	}

	public ArrayList<RecorderUnit> getRecording() {
		return recording;
	}

	public JSONCar getCarJson() {
		return carJson;
	}

	public String getTrackObjectId() {
		return trackObjectId;
	}

	public TrackMode getTrackMode() {
		return trackMode;
	}

	public TrackType getTrackType() {
		return trackType;
	}

	public String getTrackIndex() {
		return trackIndex;
	}

	public Integer getTrackDifficulty() {
		return Integer.parseInt(trackDifficulty);
	}

}
