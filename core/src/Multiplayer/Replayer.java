package Multiplayer;

import java.util.ArrayList;

import wrapper.Globals;
import wrapper.TouchUnit;
import Assembly.AssembledObject;
import GroundWorks.GroundBuilder;
import Multiplayer.Recorder.RecoderTouchType;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Replayer {

	private ArrayList<RecorderUnit> recording;
	private Integer segmentReplayPointer = 0;
	private Integer segmentReplayPointerPOS = 0;

	private long[] recordedTimes;
	private float[] differenceTimes;
	private RecoderTouchType[] recordedTypes;
	private float[] recordedX, recordedY, recordedRotation;

	private ArrayList<ArrayList<Integer>> recordedBrokenJoints = new ArrayList<ArrayList<Integer>>();

	private ArrayList<TouchUnit> noneBothTouches = new ArrayList<TouchUnit>();

	// private int timeSync = 0;

	private ArrayList<TouchUnit> leftTouches = new ArrayList<TouchUnit>();
	private ArrayList<TouchUnit> rightTouches = new ArrayList<TouchUnit>();

	public Replayer(ArrayList<RecorderUnit> recordingInput) {
		recording = recordingInput;

		if (recording == null) {
			return;
		}

		int segmentPointer = 0;

		recordedTimes = new long[recordingInput.size()];
		differenceTimes = new float[recordingInput.size()];
		recordedTypes = new RecoderTouchType[recordingInput.size()];
		recordedX = new float[recordingInput.size()];
		recordedY = new float[recordingInput.size()];
		recordedRotation = new float[recordingInput.size()];
		for (RecorderUnit segment : recordingInput) {

			recordedTimes[segmentPointer] = segment.getTime();
			recordedTypes[segmentPointer] = segment.getTouchType();
			differenceTimes[segmentPointer] = segment.getDifference();
			recordedX[segmentPointer] = segment.getX();
			recordedY[segmentPointer] = segment.getY();
			recordedRotation[segmentPointer] = segment.getRotation();
			recordedBrokenJoints.add(segment.getBrokenJoints());
			segmentPointer++;
		}

		TouchUnit fakeTouch = new TouchUnit();
		fakeTouch.screenX = (Globals.ScreenWidth / 2f) - 100;
		fakeTouch.touched = true;
		leftTouches.add(fakeTouch);

		fakeTouch = new TouchUnit();
		fakeTouch.screenX = (Globals.ScreenWidth / 2f) + 100;
		fakeTouch.touched = true;
		rightTouches.add(fakeTouch);

	}

	public Vector3 getInputPositional(long time, AssembledObject opponentCar) {
		Vector3 ret = null;

		if (segmentReplayPointerPOS + 1 >= recordedTimes.length) {
			ret = new Vector3(recordedX[segmentReplayPointerPOS],
					recordedY[segmentReplayPointerPOS],
					recordedRotation[segmentReplayPointerPOS]);
		} else if (time >= recordedTimes[segmentReplayPointerPOS + 1]) {

			segmentReplayPointerPOS++;
			ret = new Vector3(recordedX[segmentReplayPointerPOS],
					recordedY[segmentReplayPointerPOS],
					recordedRotation[segmentReplayPointerPOS]);

		} else if (time >= recordedTimes[segmentReplayPointerPOS]) {

			float ratioInverse = 0;
			// segment / GamePlayScreen.OPPONENT_RECORDER_SKIP;
			float ratio = 1;// - ratioInverse;
			ret = new Vector3(
					((recordedX[segmentReplayPointerPOS] * ratio + recordedX[segmentReplayPointerPOS + 1]
							* ratioInverse)),
					((recordedY[segmentReplayPointerPOS] * ratio + recordedY[segmentReplayPointerPOS + 1]
							* ratioInverse)),
					((recordedRotation[segmentReplayPointerPOS] * ratio + recordedRotation[segmentReplayPointerPOS + 1]
							* ratioInverse)));

		} else {
			ret = new Vector3(recordedX[segmentReplayPointerPOS],
					recordedY[segmentReplayPointerPOS],
					recordedRotation[segmentReplayPointerPOS]);
		}

		return ret;
	}

	public enum TravalDirection {
		POS_X, NEG_X, POS_Y, NEG_Y
	};

	//float timeSync;
	boolean directionX = true; // forward
	private boolean finalCall = true;

	public ArrayList<TouchUnit> getInputTypeWise(long time, float difference,
			float x, float y, GroundBuilder ground,
			AssembledObject opponentCar, World world) {
		//timeSync = ((difference - differenceTimes[segmentReplayPointer]) * Globals.STEP);
		// time += timeSync;

		if (segmentReplayPointer + 1 >= recordedTimes.length) {

			if(finalCall){
			opponentCar.setAbsPosition(recordedX[segmentReplayPointer],
					recordedY[segmentReplayPointer],
					recordedRotation[segmentReplayPointer]);
			finalCall = false;
			}

			return touchTypeToArray(recordedTypes[segmentReplayPointer]);

		}

		// directionX = recordedX[segmentReplayPointer] <
		// recordedX[segmentReplayPointer + 1];

		if (
		// ((directionX) && (x >= recordedX[segmentReplayPointer + 1]))
		// || ((!directionX) && (x <= recordedX[segmentReplayPointer + 1]))

		time >= recordedTimes[segmentReplayPointer + 1]

		// difference >= differenceTimes[segmentReplayPointer+1]

		) {

			if (recordedTypes[segmentReplayPointer + 1] == RecoderTouchType.JOINT_BREAK) {

				Array<Joint> joints = new Array<Joint>();
				world.getJoints(joints);

				for (Joint joint : joints) {
					if (joint.getUserData() == null) {
						continue;
					}

					if (recordedBrokenJoints.get(segmentReplayPointer + 1)
							.contains(((Integer) joint.getUserData()) * -1)) {
						world.destroyJoint(joint);
					}
				}

				recordedTypes[segmentReplayPointer + 1] = recordedTypes[segmentReplayPointer];

			} else if (recordedTypes[segmentReplayPointer + 1] == RecoderTouchType.POSITION) {

				recordedTypes[segmentReplayPointer + 1] = recordedTypes[segmentReplayPointer];

			} else {

				segmentReplayPointer++;
			}

			opponentCar.setAbsPosition(recordedX[segmentReplayPointer],
					recordedY[segmentReplayPointer],
					recordedRotation[segmentReplayPointer]);

			System.out
					.println(time
							+ "/"
							+ recordedTimes[segmentReplayPointer]
							+ " "
							+ opponentCar.getPosition().x
							+ " "
							+ recordedRotation[segmentReplayPointer]
							+ " switch "
							+ recordedX[segmentReplayPointer]
							+ " actual: "
							+ recordedTypes[segmentReplayPointer].toString()
							+ " error: "
							+ (100 * ((float) x / recordedX[segmentReplayPointer]) - 100));

		}

		return touchTypeToArray(recordedTypes[segmentReplayPointer]);

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
