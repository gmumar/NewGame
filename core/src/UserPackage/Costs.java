package UserPackage;

import java.util.HashMap;

import Component.ComponentNames;

public class Costs {

	static private final HashMap<Integer, Integer> bar3Costs = new HashMap<Integer, Integer>();
	static private final HashMap<Integer, Integer> tireCosts = new HashMap<Integer, Integer>();
	static private final HashMap<Integer, Integer> springCosts = new HashMap<Integer, Integer>();
	
	static public final Integer ADVENTURE_TRACK = 1000;
	public static final Integer INFINITY_TRACK_MODE = 2500;
	public static final Integer INFINITY_TRACK = 2000;
	public static final Integer COMMUNITY_CARS_MODE = 2500;
	static public final Integer ARCTIC_WORLD = 25000;
	
	static {
		bar3Costs.put(1, 500);
		bar3Costs.put(2, 1000);//wood
		bar3Costs.put(3, 3000);
		bar3Costs.put(4, 5000);
		bar3Costs.put(5, 8000);
		bar3Costs.put(6, 10000);//metal
		bar3Costs.put(7, 20000);
		bar3Costs.put(8, 25000);
		bar3Costs.put(9, 30000);
		bar3Costs.put(10, 40000);//carbon
		bar3Costs.put(11, 50000);
		bar3Costs.put(12, 55000);//diamond
		bar3Costs.put(13, 70000);
		bar3Costs.put(14, 85000);
		bar3Costs.put(15, 100000);//aero
		
		tireCosts.put(1, 500);
		tireCosts.put(2, 1000);
		tireCosts.put(3, 3000);
		tireCosts.put(4, 5000);
		tireCosts.put(5, 8000);
		tireCosts.put(6, 10000);
		tireCosts.put(7, 20000);
		tireCosts.put(8, 25000);
		tireCosts.put(9, 30000);
		tireCosts.put(10, 40000);
		tireCosts.put(11, 50000);
		tireCosts.put(12, 55000);
		tireCosts.put(13, 70000);
		tireCosts.put(14, 85000);
		tireCosts.put(15, 100000);
		
		springCosts.put(1, 500);
		springCosts.put(2, 1000);
		springCosts.put(3, 3000);
		springCosts.put(4, 5000);
		springCosts.put(5, 8000);
		springCosts.put(6, 10000);
		springCosts.put(7, 20000);
		springCosts.put(8, 25000);
		springCosts.put(9, 30000);
		springCosts.put(10, 40000);
		springCosts.put(11, 50000);
		springCosts.put(12, 55000);
		springCosts.put(13, 70000);
		springCosts.put(14, 85000);
		springCosts.put(15, 100000);
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
