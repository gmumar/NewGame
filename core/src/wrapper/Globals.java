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

	final static public int GameHeight = 440;
	final static public int GameWidth = 710;
	
	final static public Color BLUE = new Color(0.3f, 0.3f, 1, 1);
	final static public Color SKY_BLUE = new Color((float) 118 / 256, (float) 211 / 256,
			(float) 222 / 256, 1);
	final static public Color GREEN = new Color((float)13/256,(float)142/256,(float)0/256, 1.0f);
	final static public Color GREEN1 = new Color((float)20/256,(float)213/256,(float)0/256, 1.0f);
	
	final static public Color BROWN1 =new Color(0.73f, 0.40f, 0.31f, 1);	
	final static public Color TRANSPERENT_BLACK = new Color(0,0,0, 0.4f);

	final public static float PIXEL_TO_METERS = 125;
	final public static int MAX_FINGERS = 2;
	final public static int VERSION = 3;
	

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
					&& joinIn.props != null
					&& joinIn.props.size() == join.props.size()) {
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
