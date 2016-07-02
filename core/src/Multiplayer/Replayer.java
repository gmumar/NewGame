package Multiplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import wrapper.Globals;
import wrapper.TouchUnit;
import Multiplayer.Recorder.RecoderTouchType;

public class Replayer {

	private Recorder recording;
	private Integer segmentReplayPointer = 0;

	private float[] recordedTimes;
	private RecoderTouchType[] recordedTypes;

	private ArrayList<TouchUnit> leftTouches = new ArrayList<TouchUnit>();
	private ArrayList<TouchUnit> rightTouches = new ArrayList<TouchUnit>();
	private ArrayList<TouchUnit> noneBothTouches = new ArrayList<TouchUnit>();

	public Replayer(String recordingInput) {
		recording = Recorder.objectify(recordingInput);

		if (recording == null) {
			return;
		}

		ArrayList<RecorderUnit> recordedSegments = recording.getRecording();

		Collections.sort(recordedSegments, new Comparator<RecorderUnit>() {

			@Override
			public int compare(RecorderUnit o1, RecorderUnit o2) {

				Float table1 = o1.getTime();
				Float table2 = o2.getTime();

				return table1.compareTo(table2);
			}

		});

		int segmentPointer = 0;

		recordedTimes = new float[recordedSegments.size()];
		recordedTypes = new RecoderTouchType[recordedSegments.size()];

		for (RecorderUnit segment : recordedSegments) {

			recordedTimes[segmentPointer] = segment.getTime();
			recordedTypes[segmentPointer] = segment.getTouchType();
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

	public ArrayList<TouchUnit> getInput(float time) {
		if (segmentReplayPointer + 1 >= recordedTimes.length) {
			return touchTypeToArray(recordedTypes[segmentReplayPointer]);
		} else if (time >= recordedTimes[segmentReplayPointer + 1]) {
			segmentReplayPointer++;
			return touchTypeToArray(recordedTypes[segmentReplayPointer]);
		} else {
			return touchTypeToArray(recordedTypes[segmentReplayPointer]);
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
