package Multiplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import wrapper.Globals;
import wrapper.TouchUnit;
import Multiplayer.Recorder.RecoderTouchType;

import com.badlogic.gdx.math.Vector3;
import com.gudesigns.climber.GamePlayScreen;

public class Replayer {

	private ArrayList<RecorderUnit> recording;
	private Integer segmentReplayPointer = 0;

	private int[] recordedTimes;
	private float[] differenceTimes;
	private RecoderTouchType[] recordedTypes;
	private float[] recordedX, recordedY, recordedRotation;

	private ArrayList<TouchUnit> leftTouches = new ArrayList<TouchUnit>();
	private ArrayList<TouchUnit> rightTouches = new ArrayList<TouchUnit>();
	private ArrayList<TouchUnit> noneBothTouches = new ArrayList<TouchUnit>();

	// private int timeSync = 0;

	public Replayer(ArrayList<RecorderUnit> recordingInput) {
		recording = recordingInput;

		if (recording == null) {
			return;
		}

		ArrayList<RecorderUnit> recordedSegments = recording;

		Collections.sort(recordedSegments, new Comparator<RecorderUnit>() {

			@Override
			public int compare(RecorderUnit o1, RecorderUnit o2) {

				Integer table1 = o1.getTime();
				Integer table2 = o2.getTime();

				return table1.compareTo(table2);
			}

		});

		int segmentPointer = 0;

		recordedTimes = new int[recordedSegments.size()];
		differenceTimes = new float[recordedSegments.size()];
		// recordedTypes = new RecoderTouchType[recordedSegments.size()];
		recordedX = new float[recordedSegments.size()];
		recordedY = new float[recordedSegments.size()];
		recordedRotation = new float[recordedSegments.size()];

		for (RecorderUnit segment : recordedSegments) {

			recordedTimes[segmentPointer] = segment.getTime();
			// recordedTypes[segmentPointer] = segment.getTouchType();
			differenceTimes[segmentPointer] = segment.getDifference();
			recordedX[segmentPointer] = segment.getX();
			recordedY[segmentPointer] = segment.getY();
			recordedRotation[segmentPointer] = segment.getRotation();
			segmentPointer++;
		}

		TouchUnit fakeTouch = new TouchUnit();
		fakeTouch.screenX = Globals.ScreenWidth - 1;
		fakeTouch.touched = true;
		leftTouches.add(fakeTouch);

		fakeTouch.screenX = Globals.ScreenWidth + 1;
		fakeTouch.touched = true;
		rightTouches.add(fakeTouch);
	}

	public Vector3 getInputPositional(int time) {
		Vector3 ret = null;

		if (segmentReplayPointer + 1 >= recordedTimes.length) {
			ret = new Vector3(recordedX[segmentReplayPointer],
					recordedY[segmentReplayPointer],
					recordedRotation[segmentReplayPointer]);
		} else if (time >= recordedTimes[segmentReplayPointer + 1]) {

			segmentReplayPointer++;
			ret = new Vector3(recordedX[segmentReplayPointer],
					recordedY[segmentReplayPointer],
					recordedRotation[segmentReplayPointer]);

		} else if (time >= recordedTimes[segmentReplayPointer]) {

			int segment = time - recordedTimes[segmentReplayPointer];
			float ratioInverse = (float) segment
					/ GamePlayScreen.OPPONENT_RECORDER_SKIP;
			float ratio = 1 - ratioInverse;
			ret = new Vector3(
					(recordedX[segmentReplayPointer] * ratio + recordedX[segmentReplayPointer + 1]
							* ratioInverse),
					(recordedY[segmentReplayPointer] * ratio + recordedY[segmentReplayPointer + 1]
							* ratioInverse),
					(recordedRotation[segmentReplayPointer] * ratio + recordedRotation[segmentReplayPointer + 1]
							* ratioInverse));

		} else {
			ret = new Vector3(recordedX[segmentReplayPointer],
					recordedY[segmentReplayPointer],
					recordedRotation[segmentReplayPointer]);
		}

		return ret;
	}

	public ArrayList<TouchUnit> getInputTypeWise(int time, float difference) {
		// timeSync = (int)((difference -
		// differenceTimes[segmentReplayPointer])*Globals.STEP/2);
		// time -= timeSync;

		if (segmentReplayPointer + 1 >= recordedTimes.length) {

			return touchTypeToArray(recordedTypes[segmentReplayPointer]);

		} else if (time >= recordedTimes[segmentReplayPointer + 1]) {
			// if (difference - differenceTimes[segmentReplayPointer])
			// is positive than the simulation is running slower than the
			// recorder
			// if the simulation is running slower than push out switch
			// to push out switch subtract from time.

			segmentReplayPointer++;

			return touchTypeToArray(recordedTypes[segmentReplayPointer]);

		} else if (time >= recordedTimes[segmentReplayPointer]) {

			return touchTypeToArray(recordedTypes[segmentReplayPointer]);

		} else {
			System.out.println("NONE at " + time + " actual: "
					+ recordedTimes[segmentReplayPointer]);
			return touchTypeToArray(recordedTypes[segmentReplayPointer]);
			// return touchTypeToArray(RecoderTouchType.NONE);
		}
	}

	private ArrayList<TouchUnit> touchTypeToArray(RecoderTouchType type) {

		if (type == RecoderTouchType.LEFT) {
			return leftTouches;
		} else if (type == RecoderTouchType.RIGHT) {
			return rightTouches;
		} else {
			return noneBothTouches;
		}
	}

}
