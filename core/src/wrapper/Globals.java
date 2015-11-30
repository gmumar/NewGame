package wrapper;

import java.util.ArrayList;
import java.util.Iterator;

import Assembly.Assembler;
import JSONifier.JSONJoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class Globals {

	static public int ScreenHeight;
	static public int ScreenWidth;
	static public float AspectRatio;

	static public int GameHeight = 480;
	static public int GameWidth = 640;
	
	static public Color BLUE = new Color(0.3f, 0.3f, 1, 1);

	final public static float PIXEL_TO_METERS = 125;
	final public static int MAX_FINGERS = 2;

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

	public static boolean contains(ArrayList<JSONJoint> jointExclusionList,
			JSONJoint joinIn) {

		if (jointExclusionList.isEmpty())
			return false;

		Iterator<JSONJoint> JointIter = jointExclusionList.iterator();
		JSONJoint join;

		while (JointIter.hasNext()) {
			join = JointIter.next();

			if (joinIn.getMount1().compareTo(join.getMount1()) == 0
					&& joinIn.getMount2().compareTo(join.getMount2()) == 0
					&& joinIn.properties != null
					&& joinIn.properties.size() == join.properties.size()) {
				return true;
			}

		}
		return false;
	}
	
	public static String getId(String name) {
		return name.split(Assembler.NAME_ID_SPLIT)[1];
	}

	public static String getSubname(String name) {
		String subpart = name.split(Assembler.NAME_SUBNAME_SPLIT)[1];
		return subpart.split(Assembler.NAME_ID_SPLIT)[0];
	}

	public static int getMountId(String name) {
		return Integer.parseInt(parseName(name)[1]);
	}

	public static String[] parseName(String name) {
		// e.g. bar3_0 , 1
		return name.split(Assembler.NAME_MOUNT_SPLIT);
	}

	public static String getComponentName(String name) {
		// e.g. bar3
		return name.split(Assembler.NAME_ID_SPLIT)[0];
	}

}
