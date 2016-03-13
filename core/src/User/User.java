package User;

import Component.ComponentNames;

public class User {

	private Integer smallBarLevel = 6;
	private Integer tireLevel = 1;
	private Integer springLevel = 6;
	private Integer money = 10000;

	private static User instance;

	private User() {

	}

	public Integer getLevel(String partName) {

		if (partName.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			return getSpringLevel();
		} else if (partName.compareTo(ComponentNames.WHEEL) == 0) {
			return getTireLevel();
		} else if (partName.compareTo(ComponentNames.BAR3) == 0) {
			return getSmallBarLevel();
		}
		return 1;
	}

	public static User getInstance() {
		if (instance == null) {
			instance = new User();
		}
		return instance;
	}

	public Integer getSmallBarLevel() {
		if (smallBarLevel <= 1) {
			return 1;
		}
		return new Integer(smallBarLevel);
	}

	public void setSmallBarLevel(Integer smallBarLevel) {
		this.smallBarLevel = smallBarLevel;
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

	public void decrementTireLevel() {
		tireLevel--;
		if (tireLevel <= 1) {
			tireLevel = 1;
		}
	}

	public Integer getSpringLevel() {
		if (springLevel <= 1) {
			return 1;
		}
		return new Integer(springLevel);
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

	public void setMoney(Integer money) {
		this.money = money;
	}

}
