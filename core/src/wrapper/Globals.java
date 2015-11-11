package wrapper;



public class Globals {

	static public int ScreenHeight;
	static public int ScreenWidth;
	static public float AspectRatio;
	
	final public static float PIXEL_TO_METERS = 125;
	final public static int MAX_FINGERS = 2;
	
	static public float convertToDegrees(float radians){
		return (float)(radians*(180/Math.PI));
	}
	
	static private void calculateAspectRatio(){
		AspectRatio = (float)ScreenHeight/(float)ScreenWidth;
	}

	public static void updateScreenInfo(int width, int height) {
		ScreenWidth = width;
		ScreenHeight = height;
		calculateAspectRatio();
	}
	
	public static float PixelToMeters(float pixel){
		return pixel/PIXEL_TO_METERS;
	}
}
