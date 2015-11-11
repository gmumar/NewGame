package wrapper;

public class TouchUnit {
	public float screenX = 0;
	public float screenY = 0;
	public boolean touched = false;

	public TouchUnit() {
		screenX = screenY = 0;
		touched = false;
	}

	public boolean isTouched() {
		return touched;
	}
}
