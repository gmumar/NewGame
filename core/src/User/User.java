package User;

import wrapper.GamePreferences;
import wrapper.Globals;
import Component.ComponentNames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class User {

	public static final Integer MAX_BAR3_LEVEL = 15;
	public static final Integer MAX_TIRE_LEVEL = 5;
	public static final Integer MAX_SPRING_LEVEL = 15;

	private Integer smallBarLevel = 1;
	private Integer tireLevel = 1;
	private Integer springLevel = 1;
	private Integer money = 10000;
	private String currentCar = null;
	private String currentTrack = null;

	private static User instance;

	private User() {
		currentCar = (Globals.defualt_car);
		currentTrack = (Globals.default_track);
	}

	public String getCurrentCar() {
		if (currentCar == null) {
			Preferences prefs = Gdx.app
					.getPreferences(GamePreferences.CAR_PREF_STR);

			String inputString = prefs.getString(GamePreferences.CAR_MAP_STR,
					Globals.defualt_car);
			currentCar = inputString;
		}

		return currentCar;
	}

	public void setCurrentCar(String currentCar) {
		Preferences prefs = Gdx.app
				.getPreferences(GamePreferences.CAR_PREF_STR);

		prefs.putString(GamePreferences.CAR_MAP_STR, currentCar);
		prefs.flush();

		System.out.println("wrting current car in user " + currentCar);

		this.currentCar = currentCar;
	}

	public String getCurrentTrack() {
		return currentTrack;
	}

	public void setCurrentTrack(String currentTrack) {
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
	
	public Integer addCoin(Integer value){
		money += value;
		return money;
	}

	public Integer getSmallBarLevel() {
		if (smallBarLevel <= 1) {
			return 1;
		}
		return new Integer(smallBarLevel);
	}

	public void incrementSmallBarLevel() {
		smallBarLevel++;
		if (smallBarLevel >= MAX_BAR3_LEVEL) {
			smallBarLevel = MAX_BAR3_LEVEL;
		}
	}

	public void decrementSmallBarLevel() {
		smallBarLevel--;
		if (smallBarLevel <= 1) {
			smallBarLevel = 1;
		}
	}

	public Integer getTireLevel() {
		if (tireLevel <= 1) {
			return 1;
		}
		return new Integer(tireLevel);
	}

	public void setTireLevel(Integer tireLevel) {
		this.tireLevel = tireLevel;
	}

	public void incrementTireLevel() {
		tireLevel++;
		if (tireLevel >= MAX_TIRE_LEVEL) {
			tireLevel = MAX_TIRE_LEVEL;
		}
	}

	public Integer getSpringLevel() {
		if (springLevel <= 1) {
			return 1;
		}
		return new Integer(springLevel);
	}

	public void incrementSpringLevel() {
		springLevel++;
		if (springLevel >= MAX_SPRING_LEVEL) {
			springLevel = MAX_SPRING_LEVEL;
		}
	}

	public void setSpringLevel(Integer springLevel) {
		this.springLevel = springLevel;
	}

	public void decrementSpringLevel() {
		springLevel--;
		if (springLevel <= 1) {
			springLevel = 1;
		}
	}

	public Integer getMoney() {
		return money;
	}

	public void decrementMoney(Integer value) {
		this.money -= value;
	}

}
