package Multiplayer;

import Multiplayer.Recorder.RecoderTouchType;

public class RecorderUnit {

	private RecoderTouchType tt;
	private float t;

	public RecorderUnit(float time, RecoderTouchType type) {
		super();
		this.t = time;
		this.tt = type;
	}

	public float getTime() {
		return t;
	}

	public RecoderTouchType getTouchType() {
		return tt;
	}
}
