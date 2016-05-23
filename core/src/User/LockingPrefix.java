package User;

public class LockingPrefix {
	
	public final static String INFINITY = "infin";
	public final static String ADVENTURE_STRING = "adven";
	public final static String CAR_BUILDER = "car_build";
	public final static String CAR_MY_PICKS = "car_pick";
	public final static String CAR_COMMUNITY_CAR = "car_comm";
	
	public final static String SPLITTER = "_";
	
	public final static String getForrestPrefix(){
		return "f" + SPLITTER;
	}
	
	public final static String getArticPrefix(){
		return "a" + SPLITTER;
	}
	
	public final static String getModePrefix(){
		return "m" + SPLITTER;
	}
	
}
