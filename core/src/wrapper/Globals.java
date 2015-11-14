package wrapper;

import com.badlogic.gdx.Gdx;

public class Globals {

	static public int ScreenHeight;
	static public int ScreenWidth;
	static public float AspectRatio;
	
	static public int GameHeight = 480;
	static public int GameWidth = 640;
	
	final public static float PIXEL_TO_METERS = 125;
	final public static int MAX_FINGERS = 2;
	
	static public float convertToDegrees(float radians){
		return (float)(radians*(180/Math.PI));
	}
	
	static private void calculateAspectRatio(){
		AspectRatio = (float)ScreenHeight/(float)ScreenWidth;
	}

	public static void updateScreenInfo() {	
		
		//int heightRatio = Gdx.graphics.getHeight()/GameHeight;
		int widthRatio = Gdx.graphics.getWidth()/GameWidth;
		
		ScreenWidth = GameWidth * widthRatio;
		ScreenHeight = GameHeight;
		calculateAspectRatio();
	}
	
	public static float PixelToMeters(float pixel){
		return pixel/PIXEL_TO_METERS;
	}
	
}
