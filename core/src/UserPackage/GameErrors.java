package UserPackage;

public class GameErrors {
	
	public final static String PARTS_NOT_UNLOCKED = "You have not yet unlocked all parts required for this car";
	public final static String CAR_TOO_HIGH_TO_USE = "You cannot use a car with higher level parts than you opponent"
			+ " \n\t Max Bar level: " + User.getInstance().getOpponentBarLevel()
			+ " \n\t Max Spring level: " + User.getInstance().getOpponentSpringLevel()
			+ " \n\t Max Tire level: " + User.getInstance().getOpponentTireLevel();

}
