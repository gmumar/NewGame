package Component;

import java.util.HashMap;

public class ComponentNames {

	public final static String BAR3 = "B3";
	public final static String TIRE = "TR";
	public final static String SOLIDJOINT = "SJ";
	public final static String AXLE = "AX";
	public final static String SPRINGJOINT = "SPJ";
	public final static String WHEEL = "WL";
	public final static String LIFE = "LF";
	public final static String CEQUEREDFLAG = "CF";
	public final static String CAMERAFOCUS = "CMF";
	public static final String GROUND = "GND";

	public static final String TRACK_NAME_PREFIX = "T_";
	public static final String POST = TRACK_NAME_PREFIX + "P";
	public static final String TOUCHABLE_POST = TRACK_NAME_PREFIX + "tP";
	public static final String TRACKBAR = TRACK_NAME_PREFIX + "B";
	public static final String TRACKBALL = "BA";
	public static final String TRACKCOIN = "CN";
	
	public final static HashMap<String, String> PART_NAME_MAPPING = new HashMap<String, String>();

	static {
		PART_NAME_MAPPING.put(BAR3, "bar");
		PART_NAME_MAPPING.put(TIRE, "tire");
		PART_NAME_MAPPING.put(AXLE, "tire");
		PART_NAME_MAPPING.put(SPRINGJOINT, "spring");
	}
}
