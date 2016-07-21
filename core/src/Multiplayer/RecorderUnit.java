package Multiplayer;

import java.util.ArrayList;

import Multiplayer.Recorder.RecoderTouchType;


public class RecorderUnit {

	private RecoderTouchType tt;
	private long t;
	private float d;
	
	private float x,y,rot;
	private ArrayList<Integer> brokenJoints = new ArrayList<Integer>();
	
	private static ArrayList<Integer> dummyBrokenJoints = new ArrayList<Integer>();
	
	static {
		dummyBrokenJoints.add(-1);
	}

	public RecorderUnit(long time, float difference, float x, float y, float rot) {
		super();
		this.t = time;
		this.d = difference;
		this.x = x;
		this.y = y;
		this.rot = rot;
		this.brokenJoints = dummyBrokenJoints;
	}

	public RecorderUnit(long time, float difference, float x, float y, float rot, RecoderTouchType type) {
		super();
		this.t = time;
		this.tt = type;
		this.d = difference;
		this.x = x;
		this.y = y;
		this.rot = rot;
		this.brokenJoints = dummyBrokenJoints;
	}

	public RecorderUnit(long time, float difference, float x, float y,
			float rot, ArrayList<Integer> jointNumber,
			RecoderTouchType type) {
		super();
		this.t = time;
		this.tt = type;
		this.d = difference;
		this.x = x;
		this.y = y;
		this.rot = rot;
		this.brokenJoints.addAll(jointNumber);
	}
	
	public ArrayList<Integer> getBrokenJoints() {
		return brokenJoints;
	}

	public long getTime() {
		return t;
	}
	
	public float getX (){
		return x;
	}
	
	public float getY (){
		return y;
	}
	
	public float getRotation() {
		return rot;
	}

	public RecoderTouchType getTouchType() {
		return tt;
	}
	
	public float getDifference () {
		return d;
	}
}
