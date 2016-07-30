package UserPackage;

public class ItemsLookupPrefix {
	
	
	public final static String INFINITY_TRACK_MODE = "m_infin";
	public final static String ADVENTURE_TRACK_MODE = "m_adven";
	public final static String MULTIPLAYER_MODE = "m_multiplayer";
	public final static String ARCTIC_WORLD = "m_arctic_world";
	public final static String CAR_BUILDER = "car_build";
	public final static String CAR_MY_PICKS = "car_pick";
	public final static String COMMUNITY_CARS_MODE = "car_comm";
	public final static String NO_ADS = "no_ads";
	public final static String USER_NAME = "usr_name";
	private final static String USER_OBJ_ID = "usr_obj_id";
	
	public final static String SPLITTER = "_";

	public static final String ERROR_NOT_ENOUGH_MONEY = "_not_enough_money_";
	public static final String ERROR_PARTS_NOT_UNLOCKED = "_parts_not_unlocked_";
	
	public final static String getForrestPrefix(String mapIndex){
		return "_forrest" + SPLITTER + mapIndex;
	}
	
	public final static String getArticPrefix(String mapIndex){
		return "_artic" + SPLITTER + mapIndex;
	}
	
	public final static String getInfiniteTrackPrefix(String mapIndex){
		return "_infin" + SPLITTER + mapIndex;
	}
	
	public final static String getUserObjectIDPrefix(String userName){
		return USER_OBJ_ID + SPLITTER + userName;
	}	
}
