package throwaway;

import wrapper.BaseActor;

import com.badlogic.gdx.physics.box2d.World;

public class Car extends BaseActor {

	protected Car(String name, String texture, World world) {
		super(name, texture, world);
		
		this.setPosition(100, -100);
		//this.setScale(5);
		this.setRestitution(1);
	}



}
