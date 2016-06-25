package RESTWrapper;

public class RESTProperties {

	public static final String CAR_JSON = "car_json";
	public static final String CAR_INDEX = "carIndex";
	public static final String TRACK_POINTS_JSON = "points";
	public static final String OBJECT_ID = "objectId";
	public static final String TRACK_BEST_TIME = "bestTime";
	public static final String TRACK_DIFFICULTY = "difficulty";
	public static final String TRACK_INDEX = "mapIndex";

	public static final String CREATED = "created";
	public static final String PROPS = "props=";
	public static final String URL_ARG_SPLITTER = "?";
	public static final String PROP_PROP_SPLITTER = "%2C";
	public static final String PROP_ARG_SPLITTER = "&";
	public static final String PAGE_SIZE = "pageSize=";
	public static final String OFFSET = "offset=";
	public static final String WHERE = "where=";
	public static final String GREATER_THAN = "%3E";

	public static String WhereCreatedGreaterThan(Long lastCreatedTime) {
		return RESTProperties.WHERE + RESTProperties.CREATED
				+ RESTProperties.GREATER_THAN + lastCreatedTime;
	}
}
