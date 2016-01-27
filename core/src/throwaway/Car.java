package throwaway;

import wrapper.BaseActor;
import wrapper.GameState;

public class Car extends BaseActor {

	protected Car(String name, String texture, GameState gameState) {
		super(name, texture, gameState);
		
		this.setPosition(100, -100);
		//this.setScale(5);
		this.setRestitution(1);
	}



}
