package Multiplayer;

import java.util.ArrayList;

import wrapper.Globals;
import wrapper.TouchUnit;

import com.google.gson.Gson;

public class Recorder {

	public enum RecoderTouchType {
		NONE, LEFT, RIGHT, BOTH, END
	};

	private ArrayList<RecorderUnit> recording = new ArrayList<RecorderUnit>();
	private RecorderUnit lastUnit;
	private boolean firstUnit = true;

	public void addUnit(float time, RecoderTouchType type) {
		recording.add(new RecorderUnit(time, type));
	}

	public ArrayList<RecorderUnit> getRecording() {
		return recording;
	}
	

	public void addEndUnit(float time) {
		recording.add(new RecorderUnit(time, RecoderTouchType.END));
	}

	public void addUnit(float time, ArrayList<TouchUnit> touches) {

		boolean leftDown = false, rightDown = false;
		for (TouchUnit touch : touches) {
			if (touch.isTouched()) {
				if (touch.screenX > Globals.ScreenWidth / 2) {
					rightDown = true;
				} else {
					leftDown = true;
				}
			}
		}

		RecoderTouchType currentType;

		if (leftDown && rightDown) {
			currentType = RecoderTouchType.BOTH;
		} else if (leftDown) {
			currentType = RecoderTouchType.LEFT;
		} else if (rightDown) {
			currentType = RecoderTouchType.RIGHT;
		} else {
			currentType = RecoderTouchType.NONE;
		}

		if (firstUnit) {
			lastUnit = new RecorderUnit(time, currentType);
			recording.add(lastUnit);
			System.out.println("Recorder: added " + currentType.toString());
			firstUnit = false;
		} else {
			if (lastUnit.getTouchType() != currentType) {
				lastUnit = new RecorderUnit(time, currentType);
				recording.add(lastUnit);
				System.out.println("Recorder: added " + currentType.toString());
			}
		}

		return;

	}

	public String jsonify() {
		if (recording.isEmpty()) {
			return null;
		}
		Gson gson = new Gson();
		/*
		 * ArrayList<RecorderUnit> compressedRecording = new
		 * ArrayList<RecorderUnit>();
		 * 
		 * 
		 * RecorderUnit prevUnit, currentUnit;
		 * 
		 * compressedRecording.add(recording.get(0));
		 * 
		 * for (int i = 1; i < recording.size(); i++) { prevUnit =
		 * recording.get(i - 1); currentUnit = recording.get(i);
		 * 
		 * if (Math.abs(prevUnit.getPosition().x - currentUnit.getPosition().x)
		 * >= 0.5f || Math.abs(prevUnit.getPosition().y -
		 * currentUnit.getPosition().y) >= 0.5f) {
		 * compressedRecording.add(currentUnit); } }
		 * 
		 * System.out.println("Old: " + recording.size() + " compressed: " +
		 * compressedRecording.size() );
		 */

		return gson.toJson(this);
	}

	public static Recorder objectify(String input) {
		if (input == null) {
			return null;
		}
		Gson gson = new Gson();
		return gson.fromJson(input, Recorder.class);
	}

}
