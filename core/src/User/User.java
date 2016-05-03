package User;

import wrapper.GamePreferences;
import wrapper.Globals;
import Component.ComponentNames;
import DataMutators.TrippleDes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.google.gson.Gson;

public class User {

	public static final Integer MAX_BAR3_LEVEL = 15;
	public static final Integer MAX_TIRE_LEVEL = 5;
	public static final Integer MAX_SPRING_LEVEL = 15;

	private UserState userState = new UserState();
	private String currentCar = null;
	private String currentTrack = null;

	private static User instance;
	private TrippleDes encryptor = new TrippleDes();

	private Preferences prefs = null;

	private User() {
		if (prefs == null) {
			prefs = Gdx.app.getPreferences(GamePreferences.CAR_PREF_STR);
		}

		currentCar = (Globals.defualt_car);
		currentTrack = (Globals.default_track);
		
		restoreUserState();
	}
	
	private void saveUserState() {
		
		Gson json = new Gson();

		String encrypted = encryptor.encrypt(json.toJson(userState));

		prefs.putString(GamePreferences.USR_STR, encrypted);
		prefs.flush();

	}
	
	private void restoreUserState(){
		String inputString = prefs.getString(GamePreferences.USR_STR,
				"cveSuvxEjX1GNBw7FDuABebTQF0Dz9fsJxrLH/Mwy7GrJkIonjtO0icayx/zMMuxSm9j+vdJQwq6typf+G0FHw==");		

		Gson json = new Gson();
		userState = json.fromJson(encryptor.decrypt(inputString), UserState.class);

		//System.out.println("Restored: " + json.toJson(userState));
	}

	public String getCurrentCar() {
		String inputString = prefs.getString(GamePreferences.CAR_MAP_STR,
				Globals.defualt_car);
		currentCar = inputString;

		return currentCar;
	}

	public void setCurrentCar(String currentCar) {
		prefs.putString(GamePreferences.CAR_MAP_STR, currentCar);
		prefs.flush();

		this.currentCar = currentCar;
	}

	public String getCurrentTrack() {
		String inputString = prefs.getString(GamePreferences.TRACK_MAP_STR,
				Globals.default_track);
		
		currentTrack = inputString;
		
		return currentTrack;
	}

	public void setCurrentTrack(String currentTrack) {
		
		//prefs.clear();
		prefs.putString(GamePreferences.TRACK_MAP_STR, currentTrack);
		
		prefs.flush();
		
		this.currentTrack = currentTrack;
	}

	public Integer getLevel(String partName) {

		if (partName.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			return getSpringLevel();
		} else if (partName.compareTo(ComponentNames.TIRE) == 0
				|| partName.compareTo(ComponentNames.AXLE) == 0) {
			return getTireLevel();
		} else if (partName.compareTo(ComponentNames.BAR3) == 0) {
			return getSmallBarLevel();
		}
		return 1;
	}

	public boolean buyItem(String itemName, Integer cash) {

		decrementMoney(cash);

		if (itemName.compareTo(ComponentNames.BAR3) == 0) {
			incrementSmallBarLevel();
		} else if (itemName.compareTo(ComponentNames.TIRE) == 0
				|| itemName.compareTo(ComponentNames.AXLE) == 0) {
			incrementTireLevel();
		} else if (itemName.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			incrementSpringLevel();
		}
		
		saveUserState();

		return true;
	}

	public static User getInstance() {
		if (instance == null) {
			instance = new User();
		}
		return instance;
	}

	public static Integer getMaxLevel(String componentName) {

		if (componentName.compareTo(ComponentNames.BAR3) == 0) {
			return MAX_BAR3_LEVEL;
		} else if (componentName.compareTo(ComponentNames.TIRE) == 0
				|| componentName.compareTo(ComponentNames.AXLE) == 0) {
			return MAX_TIRE_LEVEL;
		} else if (componentName.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			return MAX_SPRING_LEVEL;
		}

		return -1;
	}

	public Integer addCoin(Integer value) {
		userState.money += value;
		saveUserState();
		return userState.money;
	}

	public Integer getSmallBarLevel() {
		if (userState.smallBarLevel <= 1) {
			return 1;
		}
		return new Integer(userState.smallBarLevel);
	}

	private void incrementSmallBarLevel() {
		userState.smallBarLevel++;
		if (userState.smallBarLevel >= MAX_BAR3_LEVEL) {
			userState.smallBarLevel = MAX_BAR3_LEVEL;
		}
		saveUserState();
	}

	public Integer getTireLevel() {
		if (userState.tireLevel <= 1) {
			return 1;
		}
		return new Integer(userState.tireLevel);
	}

	private void incrementTireLevel() {
		userState.tireLevel++;
		if (userState.tireLevel >= MAX_TIRE_LEVEL) {
			userState.tireLevel = MAX_TIRE_LEVEL;
		}
		saveUserState();
	}

	public Integer getSpringLevel() {
		if (userState.springLevel <= 1) {
			return 1;
		}
		return new Integer(userState.springLevel);
	}

	private void incrementSpringLevel() {
		userState.springLevel++;
		if (userState.springLevel >= MAX_SPRING_LEVEL) {
			userState.springLevel = MAX_SPRING_LEVEL;
		}
		saveUserState();
	}

	public Integer getMoney() {
		return userState.money;
	}

	private void decrementMoney(Integer value) {
		this.userState.money -= value;
	}

	public void setMusicPlayState(boolean playing) {
		userState.playingMusic = playing;
		saveUserState();
	}
	
	public boolean getMusicPlayState() {
		return userState.playingMusic;
	}

	public void setSfxPlayState(boolean playing) {
		userState.playingSfx = playing;
		saveUserState();
	}
	
	public boolean getSfxPlayState() {
		return userState.playingSfx;
	}
	
}
