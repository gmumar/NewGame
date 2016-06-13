package UserPackage;

import java.util.HashMap;

import Component.ComponentNames;

public class Costs {

	static private final HashMap<Integer, Integer> bar3Costs = new HashMap<Integer, Integer>();
	static private final HashMap<Integer, Integer> tireCosts = new HashMap<Integer, Integer>();
	static private final HashMap<Integer, Integer> springCosts = new HashMap<Integer, Integer>();
	
	static public final Integer ADVENTURE_TRACK = 5000;
	public static final Integer INFINITY_TRACK_MODE = 5000;
	public static final Integer INFINITY_TRACK = 2000;
	public static final Integer COMMUNITY_CARS_MODE = 2000;
	
	static public final Integer ARCTIC_WORLD = 5000;
	
	static {
		bar3Costs.put(1, 10);
		bar3Costs.put(2, 20);
		bar3Costs.put(3, 30);
		bar3Costs.put(4, 40);
		bar3Costs.put(5, 50);
		bar3Costs.put(6, 60);
		bar3Costs.put(7, 70);
		bar3Costs.put(8, 80);
		bar3Costs.put(9, 90);
		bar3Costs.put(10, 100);
		bar3Costs.put(11, 110);
		bar3Costs.put(12, 120);
		bar3Costs.put(13, 130);
		bar3Costs.put(14, 140);
		bar3Costs.put(15, 150);
		
		tireCosts.put(1, 10);
		tireCosts.put(2, 20);
		tireCosts.put(3, 30);
		tireCosts.put(4, 40);
		tireCosts.put(5, 50);
		tireCosts.put(6, 60);
		tireCosts.put(7, 70);
		tireCosts.put(8, 80);
		tireCosts.put(9, 90);
		tireCosts.put(10, 100);
		tireCosts.put(11, 110);
		tireCosts.put(12, 120);
		tireCosts.put(13, 130);
		tireCosts.put(14, 140);
		tireCosts.put(15, 150);
		
		springCosts.put(1, 10);
		springCosts.put(2, 20);
		springCosts.put(3, 30);
		springCosts.put(4, 40);
		springCosts.put(5, 50);
		springCosts.put(6, 60);
		springCosts.put(7, 70);
		springCosts.put(8, 80);
		springCosts.put(9, 90);
		springCosts.put(10, 100);
		springCosts.put(11, 110);
		springCosts.put(12, 120);
		springCosts.put(13, 130);
		springCosts.put(14, 140);
		springCosts.put(15, 150);
	}

	public static Integer lookup(String itemName, Integer itemLevel) {

		if (itemName.compareTo(ComponentNames.BAR3) == 0) {
			return bar3Costs.get(itemLevel);
		} else if (itemName.compareTo(ComponentNames.TIRE) == 0
				|| itemName.compareTo(ComponentNames.AXLE) == 0) {
			return tireCosts.get(itemLevel);
		} else if (itemName.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			return springCosts.get(itemLevel);
		}

		return -1;

	}

}
