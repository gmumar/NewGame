package User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import wrapper.GamePreferences;
import wrapper.Globals;
import Component.ComponentNames;
import DataMutators.TrippleDes;
import JSONifier.JSONCar;
import JSONifier.JSONComponent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.google.gson.Gson;

public class User {

	public static final String TRACK_PREFIX = "track_";

	public enum STARS implements Serializable {
		NONE, ONE, TWO, THREE

	}

	public static final Integer MAX_BAR3_LEVEL = 15;
	public static final Integer MAX_TIRE_LEVEL = 5;
	public static final Integer MAX_SPRING_LEVEL = 15;

	private UserState userState = new UserState();

	class LookupItemsType {
		HashMap<String, Boolean> items = new HashMap<String, Boolean>();
	};

	public LookupItemsType lockedItems = null;
	public LookupItemsType nonNewItems = null;

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
		String encryptedLocked = encryptor.encrypt(json.toJson(lockedItems));
		String encryptednonNew = encryptor.encrypt(json.toJson(nonNewItems));

		prefs.putString(GamePreferences.USR_STR, encrypted);
		prefs.putString(GamePreferences.USR_LOCKED, encryptedLocked);
		prefs.putString(GamePreferences.USR_NON_NEW, encryptednonNew);
		prefs.flush();

	}

	private void restoreUserState() {
		Gson json = new Gson();

		String inputString = prefs
				.getString(
						GamePreferences.USR_STR,
						"cveSuvxEjX1GNBw7FDuABebTQF0Dz9fsJxrLH/Mwy7GrJkIonjtO0icayx/zMMuxSm9j+vdJQwq6typf+G0FHw==");

		userState = json.fromJson(encryptor.decrypt(inputString),
				UserState.class);

		String lockedItemsString = prefs.getString(GamePreferences.USR_LOCKED,
				null);

		if (lockedItemsString != null) {
			lockedItems = json.fromJson(encryptor.decrypt(lockedItemsString),
					LookupItemsType.class);
		}

		String nonNewItemsString = prefs.getString(GamePreferences.USR_NON_NEW,
				null);

		if (nonNewItemsString != null) {
			nonNewItems = json.fromJson(encryptor.decrypt(nonNewItemsString),
					LookupItemsType.class);
		}

		// Initially unlocked
		if (lockedItems == null) {
			lockedItems = new LookupItemsType();
		}

		lockedItems.items.put(ItemsLookupPrefix.getForrestPrefix("1"), false);
		lockedItems.items.put(ItemsLookupPrefix.getForrestPrefix("2"), false);
		lockedItems.items.put(ItemsLookupPrefix.getForrestPrefix("3"), false);

		saveUserState();
		// System.out.println("Restored: " + json.toJson(userState));
	}

	public String getCurrentCar() {
		String inputString = prefs.getString(GamePreferences.CAR_MAP_STR,
				Globals.defualt_car);
		currentCar = inputString;

		return currentCar;
	}

	// return true if all parts are available to user
	public boolean setCurrentCar(String currentCar, boolean test) {

		JSONCar car = JSONCar.objectify(currentCar);
		ArrayList<JSONComponent> parts = car.getComponentList();

		Integer barLevel = 0, springLevel = 0, tireLevel = 0;

		for (JSONComponent part : parts) {
			if (part.getBaseName().compareTo(ComponentNames.BAR3) == 0) {
				if (part.getjComponentName().getLevel() > barLevel) {
					barLevel = part.getjComponentName().getLevel();
				}
			} else if (part.getBaseName().compareTo(ComponentNames.SPRINGJOINT) == 0) {
				if (part.getjComponentName().getLevel() > springLevel) {
					springLevel = part.getjComponentName().getLevel();
				}
			} else if (part.getBaseName().compareTo(ComponentNames.TIRE) == 0
					|| part.getBaseName().compareTo(ComponentNames.AXLE) == 0) {
				if (part.getjComponentName().getLevel() > tireLevel) {
					tireLevel = part.getjComponentName().getLevel();
				}
			}
		}
		
		if(barLevel > userState.smallBarLevel){
			return false;
		}
		
		if(springLevel > userState.springLevel){
			return false;
		}
		
		if(tireLevel > userState.tireLevel){
			return false;
		}

		if(!test){
			prefs.putString(GamePreferences.CAR_MAP_STR, currentCar);
			prefs.flush();
	
			this.currentCar = currentCar;
		}

		return true;
	}

	public String getCurrentTrack() {
		String inputString = prefs.getString(GamePreferences.TRACK_MAP_STR,
				Globals.default_track);

		currentTrack = inputString;

		return currentTrack;
	}

	public void setCurrentTrack(String currentTrack, TrackMode mode) {

		// prefs.clear();

		System.out.println("User: Track saved!");

		prefs.putString(GamePreferences.TRACK_MAP_STR, currentTrack);
		prefs.putString(GamePreferences.TRACK_MODE_STR, mode.toString());
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

	public Integer buyItem(String itemName, Integer cash) {

		Integer moneyRequired = decrementMoney(cash);

		if (moneyRequired == -1) {
			if (itemName.compareTo(ComponentNames.BAR3) == 0) {
				incrementSmallBarLevel();
			} else if (itemName.compareTo(ComponentNames.TIRE) == 0
					|| itemName.compareTo(ComponentNames.AXLE) == 0) {
				incrementTireLevel();
			} else if (itemName.compareTo(ComponentNames.SPRINGJOINT) == 0) {
				incrementSpringLevel();
			} else if (itemName
					.contains(ItemsLookupPrefix.getForrestPrefix(""))
					|| itemName.contains(ItemsLookupPrefix.getArticPrefix(""))) {
				Unlock(itemName);
			} else if (itemName
					.compareTo(ItemsLookupPrefix.COMMUNITY_CARS_MODE) == 0) {
				Unlock(ItemsLookupPrefix.COMMUNITY_CARS_MODE);
			} else if (itemName.contains(ItemsLookupPrefix
					.getInfiniteTrackPrefix(""))) {
				Unlock(itemName);
			} else if (itemName
					.compareTo(ItemsLookupPrefix.INFINITY_TRACK_MODE) == 0) {
				Unlock(ItemsLookupPrefix.INFINITY_TRACK_MODE);
			}

			saveUserState();

			return -1;
		} else {
			return moneyRequired;
		}

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

	private Integer decrementMoney(Integer value) {
		// Always returns positive difference when not enough money unless
		// purchase successful
		if (this.userState.money - value < 0) {
			return Math.abs(this.userState.money - value);
		}

		this.userState.money -= value;
		return -1;
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

	public void setStars(String trackId, STARS stars) {

		STARS currentStars = getStars(trackId);

		if (currentStars.ordinal() >= stars.ordinal()) {
			return;
		}

		prefs.putString(TRACK_PREFIX + trackId, stars.toString());
		prefs.flush();

	}

	public STARS getStars(String trackId) {

		String starString = prefs.getString(TRACK_PREFIX + trackId,
				STARS.NONE.toString());

		if (starString.compareTo(STARS.NONE.toString()) == 0) {
			return STARS.NONE;
		} else if (starString.compareTo(STARS.ONE.toString()) == 0) {
			return STARS.ONE;
		} else if (starString.compareTo(STARS.TWO.toString()) == 0) {
			return STARS.TWO;
		} else if (starString.compareTo(STARS.THREE.toString()) == 0) {
			return STARS.THREE;
		}

		return STARS.NONE;

	}

	public void Lock(String id) {
		setLock(id, true);
	}

	public void Unlock(String id) {
		System.out.println("User: unlocking " + id);
		setLock(id, false);
	}

	private void setLock(String id, Boolean lock) {
		if (lockedItems == null) {
			lockedItems = new LookupItemsType();
		}
		lockedItems.items.put(id, lock);
		saveUserState();
	}

	public Boolean isLocked(String id) {
		System.out.println("User: checking lock " + id);
		if (lockedItems == null) {
			return true;
		}

		return lockedItems.items.get(id) == null ? true : lockedItems.items
				.get(id);
	}

	public void setNonNew(String id, Boolean isNew) {
		if (nonNewItems == null) {
			nonNewItems = new LookupItemsType();
		}
		nonNewItems.items.put(id, isNew);
		saveUserState();
	}

	public Boolean isNew(String id) {

		if (nonNewItems == null) {
			return true;
		}

		return nonNewItems.items.get(id) == null ? true : nonNewItems.items
				.get(id);
	}

	public TrackMode getCurrentTrackMode() {
		TrackMode ret = TrackMode.ADVENTURE;

		String inputString = prefs.getString(GamePreferences.TRACK_MODE_STR,
				TrackMode.ADVENTURE.toString());
		String currentTrackMode = inputString;

		if (currentTrackMode.compareTo(TrackMode.ADVENTURE.toString()) == 0) {
			ret = TrackMode.ADVENTURE;
		} else if (currentTrackMode.compareTo(TrackMode.INFINTE.toString()) == 0) {
			ret = TrackMode.INFINTE;
		}

		return ret;
	}

}
