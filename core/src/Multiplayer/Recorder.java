package Multiplayer;

import java.util.ArrayList;

import wrapper.Globals;
import wrapper.TouchUnit;

import com.google.gson.Gson;

public class Recorder {

	public enum RecoderTouchType {
		NONE, LEFT, RIGHT, BOTH, END, JOINT_BREAK, POSITION
	};

	private ArrayList<RecorderUnit> recording = new ArrayList<RecorderUnit>();
	private RecorderUnit lastUnit;
	private boolean firstUnit = true;
	public ArrayList<RecorderUnit> getRecording() {
		return recording;
	}
	
	/*public void addPositionUnit(int time, float diff, float x, float y, float rot) {
		recording.add(new RecorderUnit(time, diff, x, y, rot));
	}
	
	public void addEndPositionUnit(int time, float diff, float x, float y, float rot) {
		recording.add(new RecorderUnit(time, diff, x, y, rot));
	}*/

	public void addEndTypeUnit(int time, float difference, float x, float y, float rot) {
		//System.out.println("Recorder: added " + RecoderTouchType.END.toString() + " at " + time + " x: " + x);
		recording.add(new RecorderUnit(time, difference, x, y, rot, RecoderTouchType.END));
	}
	
	public void addJointBreakUnit(int time, float difference, float x, float y, float rot, ArrayList<Integer> jointNumber) {
		//System.out.println("Recorder: added " + RecoderTouchType.JOINT_BREAK.toString() + " at " + time + " x: " + x);
		recording.add(new RecorderUnit(time, difference, x, y, rot, jointNumber, RecoderTouchType.JOINT_BREAK));
	}
	
	public void addPositionUnit(int time, float difference, float x, float y, float rot) {
		//System.out.println("Recorder: added " + RecoderTouchType.POSITION.toString() + " at " + time + " x: " + x);
		recording.add(new RecorderUnit(time, difference, x, y, rot, RecoderTouchType.POSITION));
	}

	RecoderTouchType currentType;
	public void addTypeUnit(long time, float difference, float x, float y, float rot, ArrayList<TouchUnit> touches) {

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
			lastUnit = new RecorderUnit(time, difference, x, y, rot, currentType);
			recording.add(lastUnit);
			//System.out.println("Recorder: added " + currentType.toString() + " at " + time + " x: " + x);
			firstUnit = false;
		} else {
			if (lastUnit.getTouchType() != currentType) {
				lastUnit = new RecorderUnit(time, difference, x, y, rot, currentType);
				recording.add(lastUnit);
				//System.out.println("Recorder: added " + currentType.toString() + " at " + time + " x: " + x);
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
