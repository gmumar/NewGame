package Assembly;

public class ColliderCategories {

	public enum ColliderGroups {
		USER_CAR, OPPONENT_CAR, USER_GROUND, OPPONENT_GROUND
	}

	final static public short CAR = 2, GROUND = 4, GROUND_PART = 8,
			TOUCHABLE_GROUND_PART = 16;
	final static public short OPPONENT_CAR = 32, OPPONENT_GROUND = 64,
			OPPONENT_GROUND_PART = 128, OPPONENT_TOUCHABLE_GROUND_PART = 256;

}
