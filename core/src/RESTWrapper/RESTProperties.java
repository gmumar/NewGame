package RESTWrapper;

public class RESTProperties {

	public static final String CAR_JSON = "car_json";
	public static final String CAR_INDEX = "carIndex";
	public static final String TRACK_POINTS_JSON = "points";
	public static final String OBJECT_ID = "objectId";
	public static final String TRACK_BEST_TIME = "bestTime";
	public static final String TRACK_DIFFICULTY = "difficulty";
	public static final String TRACK_INDEX = "mapIndex";
	public static final String CHALLENGE = "challenge";
	public static final String TARGET_USER = "targetUser";
	public static final String SOURCE_USER = "sourceUser";
	public static final String CHALLENGE_REWARD = "challengeReward";
	
	public static final String CREATED = "created";
	public static final String PROPS = "props=";
	public static final String URL_ARG_SPLITTER = "?";
	public static final String PROP_PROP_SPLITTER = "%2C";
	public static final String PROP_ARG_SPLITTER = "&";
	public static final String PAGE_SIZE = "pageSize=";
	public static final String OFFSET = "offset=";
	public static final String WHERE = "where=";
	public static final String GREATER_THAN = "%3E";
	public static final String EQUALS = "%3D";
	public static final String SINGLE_QOUTE = "%27";


	public static String WhereCreatedGreaterThan(Long lastCreatedTime) {
		return RESTProperties.WHERE + RESTProperties.CREATED
				+ RESTProperties.GREATER_THAN + lastCreatedTime;
	}
	
	//where=targetUser%3D%27gmumar%27
	public static String WhereTargetUserIs(String targetUser) {
		return RESTProperties.WHERE + RESTProperties.TARGET_USER
				+ RESTProperties.EQUALS + encodeString(targetUser);
	}
	
	public static String WhereObjectIdIs(String trackObjectId) {
		return RESTProperties.WHERE + RESTProperties.OBJECT_ID
				+ RESTProperties.EQUALS + encodeString(trackObjectId);
	}
	
	private static String encodeString (String str) {
		return SINGLE_QOUTE + str + SINGLE_QOUTE; 
	}
}
