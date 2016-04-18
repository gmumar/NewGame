package wrapper;

import java.util.ArrayList;

import AdsInterface.IActivityRequestHandler;
import JSONifier.JSONJoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.async.AsyncExecutor;

public class Globals {

	static public int ScreenHeight;
	static public int ScreenWidth;
	static public float AspectRatio;
	
	final static public int GameHeight = 480;
	final static public int GameWidth = 720;
	
	final static public Color BLUE = new Color(0.3f, 0.3f, 1, 1);
	final static public Color SKY_BLUE = new Color((float) 118 / 256, (float) 211 / 256,
			(float) 222 / 256, 1);
	final static public Color GREEN = new Color((float)13/256,(float)142/256,(float)0/256, 1.0f);
	final static public Color GREEN1 = new Color((float)20/256,(float)213/256,(float)0/256, 1.0f);
	
	final static public Color BROWN1 =new Color(0.73f, 0.40f, 0.31f, 1);	
	final static public Color TRANSPERENT_BLACK = new Color(0,0,0, 0.4f);
	
	public final static Color DARK_GREEN = new Color(0, 1, 0, 1);
	public final static Color RED = new Color(1, 0, 0, 1);
	public final static Color ORANGE = new Color(0.4f, 0.8f, 0.4f, 1);

	final public static float PIXEL_TO_METERS = 125;
	final public static int MAX_FINGERS = 2;
	final public static int VERSION = 5;
	
	final public static int ROTATABLE_JOINT = 1;
	final public static int LOCKED_JOINT = 0;
	
	final public static short GROUND_GROUP = -3;
	
	final public static float STEP = 1/60f;
	final public static float STEP_INVERSE = 60f;
	
	public static final Integer DISABLE_LEVEL = -1;
	
	public static AsyncExecutor globalRunner = new AsyncExecutor(2); 
	
	public static IActivityRequestHandler nativeRequestHandler = null;
	
	public static void toast(String text){
		if(nativeRequestHandler!=null){
			nativeRequestHandler.Toast(text);
		}
	}
	
	public static void  runOnUIThread(Runnable runnable){
		Gdx.app.postRunnable(runnable);
	}
	
	static public float convertToDegrees(float radians) {
		return (float) (radians * (180 / Math.PI));
	}

	static private void calculateAspectRatio() {
		AspectRatio = (float) ScreenHeight / (float) ScreenWidth;
	}

	public static void updateScreenInfo() {

		// int heightRatio = Gdx.graphics.getHeight()/GameHeight;
		int widthRatio = Gdx.graphics.getWidth() / GameWidth;

		ScreenWidth = GameWidth * widthRatio;
		ScreenHeight = GameHeight;
		calculateAspectRatio();
	}

	public static float PixelToMeters(float pixel) {
		return pixel / PIXEL_TO_METERS;
	}

	public static float MetersToPixel(float pixel) {
		return pixel * PIXEL_TO_METERS;
	}
	
	public static boolean contains(ArrayList<JSONJoint> jointExclusionList,
			JSONJoint joinIn) {

		if (jointExclusionList.isEmpty())
			return false;



		for (JSONJoint join: jointExclusionList) {
			

			/*if (joinIn.getMount1().compareTo(join.getMount1()) == 0
					&& joinIn.getMount2().compareTo(join.getMount2()) == 0
					&& joinIn.props != null
					&& joinIn.props.size() == join.props.size()) {
				return true;
			}*/
			
			if (joinIn.getMount1().getId().compareTo(join.getMount1().getId()) == 0
					&& joinIn.getMount2().getId().compareTo(join.getMount2().getId()) == 0
					&& joinIn.props != null
					&& joinIn.props.size() == join.props.size()) {
				return true;
			}

		}
		return false;
	}
	
	public static final String defualt_car = "{\"jointList\":[{\"m1\":{\"B\":\"SPJ\",\"S\":\"_UP_\",\"C\":\"1\",\"M\":\"0\",\"L\":1},\"m2\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"2\"}},{\"m1\":{\"B\":\"SPJ\",\"S\":\"_UP_\",\"C\":\"1\",\"M\":\"0\",\"L\":1},\"m2\":{\"B\":\"B3\",\"C\":\"1\",\"M\":\"1\"}},{\"m1\":{\"B\":\"SPJ\",\"S\":\"_UP_\",\"C\":\"0\",\"M\":\"0\",\"L\":1},\"m2\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"0\"}},{\"m1\":{\"B\":\"SPJ\",\"S\":\"_UP_\",\"C\":\"0\",\"M\":\"0\",\"L\":1},\"m2\":{\"B\":\"B3\",\"C\":\"0\",\"M\":\"1\"}},{\"m1\":{\"B\":\"B3\",\"C\":\"1\",\"M\":\"1\"},\"m2\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"2\"}},{\"m1\":{\"B\":\"B3\",\"C\":\"1\",\"M\":\"0\"},\"m2\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"1\"}},{\"m1\":{\"B\":\"B3\",\"C\":\"0\",\"M\":\"2\"},\"m2\":{\"B\":\"B3\",\"C\":\"1\",\"M\":\"0\"}},{\"m1\":{\"B\":\"B3\",\"C\":\"0\",\"M\":\"2\"},\"m2\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"1\"}},{\"m1\":{\"B\":\"B3\",\"C\":\"0\",\"M\":\"1\"},\"m2\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"0\"}},{\"m1\":{\"B\":\"SPJ\",\"S\":\"_LO_\",\"C\":\"1\",\"M\":\"0\",\"L\":1},\"m2\":{\"B\":\"TR\",\"C\":\"1\",\"M\":\"0\"}},{\"m1\":{\"B\":\"TR\",\"C\":\"0\",\"M\":\"0\"},\"m2\":{\"B\":\"SPJ\",\"S\":\"_LO_\",\"C\":\"0\",\"M\":\"0\",\"L\":1}},{\"m1\":{\"B\":\"LF\",\"C\":\"0\",\"M\":\"0\"},\"m2\":{\"B\":\"B3\",\"C\":\"0\",\"M\":\"2\"}},{\"m1\":{\"B\":\"LF\",\"C\":\"0\",\"M\":\"0\"},\"m2\":{\"B\":\"B3\",\"C\":\"1\",\"M\":\"0\"}},{\"m1\":{\"B\":\"LF\",\"C\":\"0\",\"M\":\"0\"},\"m2\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"1\"}}],\"componentList\":[{\"cN\":{\"B\":\"LF\",\"S\":\"CMF\",\"C\":\"0\",\"M\":\"*\",\"L\":1},\"props\":{\"r\":\"0.0\",\"X\":\"0.0\",\"Y\":\"0.0\",\"t\":\"PART\",\"m\":false}},{\"cN\":{\"B\":\"AX\",\"C\":\"0\",\"M\":\"*\",\"L\":1},\"props\":{\"r\":\"0.0\",\"X\":\"-2.1595528\",\"Y\":\"-2.0799813\",\"t\":\"PART\",\"m\":true}},{\"cN\":{\"B\":\"SPJ\",\"S\":\"_LO_\",\"C\":\"0\",\"L\":1},\"props\":{\"r\":\"-37.595768\",\"X\":\"-2.1680284\",\"Y\":\"-2.1792185\",\"t\":\"PART\",\"m\":false}},{\"cN\":{\"B\":\"SPJ\",\"S\":\"_LO_\",\"C\":\"1\",\"L\":1},\"props\":{\"r\":\"34.72171\",\"X\":\"2.015686\",\"Y\":\"-2.224338\",\"t\":\"PART\",\"m\":false}},{\"cN\":{\"B\":\"B3\",\"C\":\"0\",\"M\":\"*\",\"L\":1},\"props\":{\"r\":\"0.0\",\"X\":\"-1.2200001\",\"Y\":\"-0.98999995\",\"t\":\"PART\",\"m\":false}},{\"cN\":{\"B\":\"B3\",\"C\":\"1\",\"M\":\"*\",\"L\":1},\"props\":{\"r\":\"0.0\",\"X\":\"1.2099998\",\"Y\":\"-0.98\",\"t\":\"PART\",\"m\":false}},{\"cN\":{\"B\":\"AX\",\"C\":\"1\",\"M\":\"*\",\"L\":1},\"props\":{\"r\":\"0.0\",\"X\":\"2.0704477\",\"Y\":\"-2.189068\",\"t\":\"PART\",\"m\":true}},{\"cN\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"*\",\"L\":1},\"props\":{\"r\":\"0.0\",\"X\":\"-0.010000348\",\"Y\":\"-0.96\",\"t\":\"PART\",\"m\":false}}],\"jointTypeList\":{},\"addComponents\":[{\"cN\":{\"B\":\"SPJ\",\"S\":\"_UP_\",\"C\":\"0\",\"L\":-1},\"props\":{\"r\":\"-37.595768\",\"X\":\"-1.2528986\",\"Y\":\"-0.9907166\",\"t\":\"PART\",\"m\":false}},{\"cN\":{\"B\":\"SPJ\",\"S\":\"_UP_\",\"C\":\"1\",\"L\":-1},\"props\":{\"r\":\"34.72171\",\"X\":\"1.1612995\",\"Y\":\"-0.9914457\",\"t\":\"PART\",\"m\":false}}]}";
	final public static String default_track = "{\"points\":[{\"x\":2.0,\"y\":0.0},{\"x\":2.0,\"y\":0.0},{\"x\":2.0,\"y\":0.0},{\"x\":4.0,\"y\":-0.666667},{\"x\":6.0,\"y\":-0.80000067},{\"x\":8.0,\"y\":-0.80000067},{\"x\":10.0,\"y\":-0.80000067},{\"x\":12.0,\"y\":-0.8666668},{\"x\":14.0,\"y\":-1.0666666},{\"x\":16.0,\"y\":-1.1333327},{\"x\":18.0,\"y\":-1.2666664},{\"x\":20.0,\"y\":-1.333334},{\"x\":22.0,\"y\":-1.2000003},{\"x\":24.0,\"y\":-0.8666668}],\"componentList\":[],\"componentJointList\":[],\"componentJointTypes\":{},\"type\":\"NORMAL\"}";


}
