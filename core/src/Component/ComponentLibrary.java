package Component;

import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class ComponentLibrary {

	private static HashMap<String,Component> library = new HashMap<String,Component>();
	private static ComponentLibrary instance = null;
	private static World componentWorld;

	public static void addComponent(String name, Component component) {
		library.put(name ,component);
	}
	
	public static Component getComponent(String name){
		Component tmp = library.get(name);
		Component ret = new Component(tmp.object, tmp.getComponentTypes(), tmp.getComponentName());
		return ret;
	}
	
	public static ComponentLibrary instantiate(){
		if(instance==null){
			instance = new ComponentLibrary();
		}
		return instance;
	}
	
	public static World getComponentWorld(){
		if(componentWorld == null){
			componentWorld = new World(new Vector2(0,0), false);
		}
		return componentWorld;
	}
}
