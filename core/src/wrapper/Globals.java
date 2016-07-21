package wrapper;

import java.math.BigDecimal;
import java.util.ArrayList;



/*
 * black #000000 - menu backgrounds, reading text, button text
 50% opacity - menu icons, unplayed levels
 white #FFFFFF - screen header, pop-up window backgrounds
 bright light #FFFBDF - coin icon and text, tap to play text
 outer glow #FFF180 at 50% opacity if you want to be extra fancy
 green #94C409 - quick start button, selected objects, empty joint, easy unlimited levels
 yellow #FFBD00 - menu buttons, medium unlimited levels
 red #FC6838 - hard unlimited levels, used joint, clear screen button
 light grey #C6C6C6 - locked menu buttons (ex. track builder mode), negative menu buttons (ex. cancel)
 med grey #757575 - builder buttons, upgrade diagrams, sound muted
 dark grey #424242 - pop-up window headers + icon text, level numbers, builder mode header background
 country green #646600 - for borders around played levels in country mode
 arctic blue #627689 - for borders around played levels in arctic mode
 
 Wood: http://www.colourlovers.com/palette/42911/Tree_of_Life
 Steel: http://www.colourlovers.com/palette/1415282/%E2%98%A3_biohazard_%E2%98%A3
 diamond: http://www.colourlovers.com/palette/919419/An_Old_Friend
 carbon fibre: http://www.colourlovers.com/palette/2842478/Racing_Fibre
 aerogel: http://www.colourlovers.com/palette/810517/aerogel
 */
import AdsInterface.IActivityRequestHandler;
import JSONifier.JSONJoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.async.AsyncExecutor;

public class Globals {

	public final static boolean ADMIN_MODE = true;
	final public static int VERSION = 13;

	volatile static public int ScreenHeight;
	volatile static public int ScreenWidth;
	volatile static public float AspectRatio;

	final static private int GameHeight = 480;
	final static private int GameWidth = 720;

	final static public Color BLUE = new Color(0.3f, 0.3f, 1, 1);
	final static public Color SKY_BLUE = new Color((float) 118 / 256,
			(float) 211 / 256, (float) 222 / 256, 1);

	public static final Color YELLOW = new Color(0xFFBD00ff);
	public static final Color GREEN = new Color(0x94C409ff);
	final static public Color FORREST_GREEN = new Color(0x646600ff);
	final static public Color FORREST_GREEN_BG = new Color(0xf2e4bdff);
	final static public Color GLOWING_LIGHT = new Color(0xFFFBDFff);
	final static public Color GREY = new Color(0xC6C6C6ff);

	final static public Color ARTIC_BLUE = new Color(0x809AB7ff);
	final static public Color ARTIC_BLUE_BG = new Color(0x91afb6ff);

	final static public Color GREEN1 = new Color((float) 20 / 256,
			(float) 213 / 256, (float) 0 / 256, 1.0f);

	final static public Color BROWN1 = new Color(0.73f, 0.40f, 0.31f, 1);
	final static public Color TRANSPERENT_BLACK = new Color(0, 0, 0, 0.4f);
	final static public Color LOCKED_COLOR = new Color(1, 1, 1, 0.9f);

	final static public Color PROGRESS_BG = new Color(0, 0, 0, 0.2f);
	final static public Color OPPONENT_PROGRESS = new Color(1, 0, 0, 0.4f);

	public final static Color DARK_GREEN = new Color(0, 1, 0, 1);
	public final static Color RED = new Color(0xFC6838FF);
	public final static Color ORANGE = new Color(0.4f, 0.8f, 0.4f, 1);

	final public static float PIXEL_TO_METERS = 125;
	final public static int MAX_FINGERS = 2;
	public static final int FILE_VERSION = 1;

	final public static int ROTATABLE_JOINT = 1;
	final public static int LOCKED_JOINT = 0;

	//final public static short GROUND_GROUP = -3;

	final public static float STEP = 0.0168f;//1 / 60f;
	final public static BigDecimal STEP_BIG = new BigDecimal("0.0168");//1 / 60f;
	final public static float STEP_INVERSE = 60f;

	public static final Integer DISABLE_LEVEL = -1;

	public static AsyncExecutor globalRunner = new AsyncExecutor(3);

	private static IActivityRequestHandler nativeRequestHandler = null;

	public static Music bgMusic;

	public final static int baseSize = 24;
	public static final float BUTTON_OPACITY = 0.5f;

	public static final int POSITION_FIRST = 1;
	public static final int POSITION_SECOND = 2;
	public static final int POSITION_THIRD = 3;
	public static final int POSITION_LOST = 4;

	public final static int CAR_DISPLAY_BUTTON_HEIGHT = baseSize * 8;
	public final static int CAR_DISPLAY_BUTTON_CAMERA_HEIGHT = baseSize * 8;
	public final static int CAR_DISPLAY_BUTTON_WIDTH = baseSize * 8;


	public static void toast(String text) {
		if (nativeRequestHandler != null) {
			nativeRequestHandler.Toast(text);
		}
	}
	
	public static void setAds(boolean state) {
		if (nativeRequestHandler != null) {
			nativeRequestHandler.showAds(state);
		}
	}
	

	public static void setNativeHandler(IActivityRequestHandler handler) {
		nativeRequestHandler = handler;
	}

	public static void runOnUIThread(Runnable runnable) {
		Gdx.app.postRunnable(runnable);
	}

	static public float convertToDegrees(float radians) {
		return (float) (radians * (180 / Math.PI));
	}

	static private void calculateAspectRatio() {
		AspectRatio = (float) ScreenHeight / (float) ScreenWidth;
	}

	public static void updateScreenInfo() {

		ScreenWidth = GameWidth;// (int) (GameWidth * widthRatio);
		ScreenHeight = GameHeight;// (int) (GameHeight * heightRatio);

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

		for (JSONJoint join : jointExclusionList) {

			/*
			 * if (joinIn.getMount1().compareTo(join.getMount1()) == 0 &&
			 * joinIn.getMount2().compareTo(join.getMount2()) == 0 &&
			 * joinIn.props != null && joinIn.props.size() == join.props.size())
			 * { return true; }
			 */

			if (joinIn.getMount1().getId().compareTo(join.getMount1().getId()) == 0
					&& joinIn.getMount2().getId()
							.compareTo(join.getMount2().getId()) == 0
					&& joinIn.props != null
					&& joinIn.props.size() == join.props.size()) {
				return true;
			}

		}
		return false;
	}

	public static String makeMoneyString(Integer money) {
		String str = money.toString();
		str.length();

		int count = str.length();

		String ret = new String();
		char c ;
		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);

			ret += c;
			count--;
			if (count % 3 == 0 && count != 0) {
				ret += ",";
			}

		}

		return ret;
	}

	public static String makeTimeStr(float input) {

		int mins = (int) (input / 60);
		int seconds = (int) (input) - mins * 60;
		int milli = (int) (input * 10) - seconds * 10 - mins * 60 * 10;

		return Integer.toString(mins).trim() + ":"
				+ Integer.toString(seconds).trim() + ":"
				+ Integer.toString(milli).trim();

	}

	public static final String defualt_car = "{\"jointList\":[{\"m1\":{\"B\":\"SPJ\",\"S\":\"_UP_\",\"C\":\"1\",\"M\":\"0\",\"L\":1},\"m2\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"2\"}},{\"m1\":{\"B\":\"SPJ\",\"S\":\"_UP_\",\"C\":\"1\",\"M\":\"0\",\"L\":1},\"m2\":{\"B\":\"B3\",\"C\":\"1\",\"M\":\"1\"}},{\"m1\":{\"B\":\"SPJ\",\"S\":\"_UP_\",\"C\":\"0\",\"M\":\"0\",\"L\":1},\"m2\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"0\"}},{\"m1\":{\"B\":\"SPJ\",\"S\":\"_UP_\",\"C\":\"0\",\"M\":\"0\",\"L\":1},\"m2\":{\"B\":\"B3\",\"C\":\"0\",\"M\":\"1\"}},{\"m1\":{\"B\":\"B3\",\"C\":\"1\",\"M\":\"1\"},\"m2\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"2\"}},{\"m1\":{\"B\":\"B3\",\"C\":\"1\",\"M\":\"0\"},\"m2\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"1\"}},{\"m1\":{\"B\":\"B3\",\"C\":\"0\",\"M\":\"2\"},\"m2\":{\"B\":\"B3\",\"C\":\"1\",\"M\":\"0\"}},{\"m1\":{\"B\":\"B3\",\"C\":\"0\",\"M\":\"2\"},\"m2\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"1\"}},{\"m1\":{\"B\":\"B3\",\"C\":\"0\",\"M\":\"1\"},\"m2\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"0\"}},{\"m1\":{\"B\":\"SPJ\",\"S\":\"_LO_\",\"C\":\"1\",\"M\":\"0\",\"L\":1},\"m2\":{\"B\":\"TR\",\"C\":\"1\",\"M\":\"0\"}},{\"m1\":{\"B\":\"TR\",\"C\":\"0\",\"M\":\"0\"},\"m2\":{\"B\":\"SPJ\",\"S\":\"_LO_\",\"C\":\"0\",\"M\":\"0\",\"L\":1}},{\"m1\":{\"B\":\"LF\",\"C\":\"0\",\"M\":\"0\"},\"m2\":{\"B\":\"B3\",\"C\":\"0\",\"M\":\"2\"}},{\"m1\":{\"B\":\"LF\",\"C\":\"0\",\"M\":\"0\"},\"m2\":{\"B\":\"B3\",\"C\":\"1\",\"M\":\"0\"}},{\"m1\":{\"B\":\"LF\",\"C\":\"0\",\"M\":\"0\"},\"m2\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"1\"}}],\"componentList\":[{\"cN\":{\"B\":\"LF\",\"S\":\"CMF\",\"C\":\"0\",\"M\":\"*\",\"L\":1},\"props\":{\"r\":\"0.0\",\"X\":\"0.0\",\"Y\":\"0.0\",\"t\":\"PART\",\"m\":false}},{\"cN\":{\"B\":\"AX\",\"C\":\"0\",\"M\":\"*\",\"L\":1},\"props\":{\"r\":\"0.0\",\"X\":\"-2.1595528\",\"Y\":\"-2.0799813\",\"t\":\"PART\",\"m\":true}},{\"cN\":{\"B\":\"SPJ\",\"S\":\"_LO_\",\"C\":\"0\",\"L\":1},\"props\":{\"r\":\"-37.595768\",\"X\":\"-2.1680284\",\"Y\":\"-2.1792185\",\"t\":\"PART\",\"m\":false}},{\"cN\":{\"B\":\"SPJ\",\"S\":\"_LO_\",\"C\":\"1\",\"L\":1},\"props\":{\"r\":\"34.72171\",\"X\":\"2.015686\",\"Y\":\"-2.224338\",\"t\":\"PART\",\"m\":false}},{\"cN\":{\"B\":\"B3\",\"C\":\"0\",\"M\":\"*\",\"L\":1},\"props\":{\"r\":\"0.0\",\"X\":\"-1.2200001\",\"Y\":\"-0.98999995\",\"t\":\"PART\",\"m\":false}},{\"cN\":{\"B\":\"B3\",\"C\":\"1\",\"M\":\"*\",\"L\":1},\"props\":{\"r\":\"0.0\",\"X\":\"1.2099998\",\"Y\":\"-0.98\",\"t\":\"PART\",\"m\":false}},{\"cN\":{\"B\":\"AX\",\"C\":\"1\",\"M\":\"*\",\"L\":1},\"props\":{\"r\":\"0.0\",\"X\":\"2.0704477\",\"Y\":\"-2.189068\",\"t\":\"PART\",\"m\":true}},{\"cN\":{\"B\":\"B3\",\"C\":\"2\",\"M\":\"*\",\"L\":1},\"props\":{\"r\":\"0.0\",\"X\":\"-0.010000348\",\"Y\":\"-0.96\",\"t\":\"PART\",\"m\":false}}],\"jointTypeList\":{},\"addComponents\":[{\"cN\":{\"B\":\"SPJ\",\"S\":\"_UP_\",\"C\":\"0\",\"L\":-1},\"props\":{\"r\":\"-37.595768\",\"X\":\"-1.2528986\",\"Y\":\"-0.9907166\",\"t\":\"PART\",\"m\":false}},{\"cN\":{\"B\":\"SPJ\",\"S\":\"_UP_\",\"C\":\"1\",\"L\":-1},\"props\":{\"r\":\"34.72171\",\"X\":\"1.1612995\",\"Y\":\"-0.9914457\",\"t\":\"PART\",\"m\":false}}]}";
	final public static String default_track = "{\"points\":[{\"x\":2.0,\"y\":0.0},{\"x\":2.0,\"y\":0.0},{\"x\":2.0,\"y\":0.0},{\"x\":4.0,\"y\":-0.666667},{\"x\":6.0,\"y\":-0.80000067},{\"x\":8.0,\"y\":-0.80000067},{\"x\":10.0,\"y\":-0.80000067},{\"x\":12.0,\"y\":-0.8666668},{\"x\":14.0,\"y\":-1.0666666},{\"x\":16.0,\"y\":-1.1333327},{\"x\":18.0,\"y\":-1.2666664},{\"x\":20.0,\"y\":-1.333334},{\"x\":22.0,\"y\":-1.2000003},{\"x\":24.0,\"y\":-0.8666668}],\"componentList\":[],\"componentJointList\":[],\"componentJointTypes\":{},\"type\":\"FORREST\"}";



}
