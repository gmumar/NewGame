package Multiplayer;

import Multiplayer.Recorder.RecoderTouchType;


public class RecorderUnit {

	private RecoderTouchType tt;
	private int t;
	private float d;
	
	private float x,y,rot;

	public RecorderUnit(int time, float difference, float x, float y, float rot) {
		super();
		this.t = time;
		//this.tt = type;
		this.d = difference;
		this.x = x;
		this.y = y;
		this.rot = rot;
	}

	public RecorderUnit(int time, float difference, RecoderTouchType type) {
		super();
		this.t = time;
		this.tt = type;
		this.d = difference;
	}

	public int getTime() {
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

	//public RecoderTouchType getTouchType() {
		//return tt;
	//}
	
	public float getDifference () {
		return d;
	}
}
