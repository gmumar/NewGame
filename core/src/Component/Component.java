package Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import wrapper.BaseActor;
import JSONifier.Properties;

import com.badlogic.gdx.physics.box2d.Body;

public class Component {

	public enum ComponentTypes {
		PART, JOINT
	}

	BaseActor object;
	ComponentTypes componentTypes;
	String componentName;
	boolean Motor = false;

	public Component(BaseActor obj, ComponentTypes type, String name) {
		object = obj;
		componentTypes = type;
		componentName = name;
	}
	

	public boolean isMotor() {
		return Motor;
	}


	public void setMotor(boolean motor) {
		Motor = motor;
	}


	public BaseActor getObject() {
		return object;
	}

	public void setObject(BaseActor object) {
		this.object = object;
	}

	public ComponentTypes getComponentTypes() {
		return componentTypes;
	}

	public void setComponentTypes(ComponentTypes componentTypes) {
		this.componentTypes = componentTypes;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public void destroyObject() {
		object.destroy();
	}

	public void applyProperties(HashMap<String, String> propertiesIn) {
		if (propertiesIn.isEmpty())
			return;

		Set<Entry<String, String>> properties = propertiesIn.entrySet();

		Iterator<Entry<String, String>> iter = properties.iterator();
		while (iter.hasNext()) {
			Entry<String, String> property = iter.next();

			if (property.getKey().compareTo(Properties.ROTATION.name()) == 0) {
				String value = property.getValue();
				this.object.setRotation(Float.parseFloat(value));

			} 
			
			if (property.getKey().compareTo(Properties.POSITION.name()) == 0) {
				String[] values = property.getValue().split(",");
				this.object.setPosition(Float.parseFloat(values[0]), Float.parseFloat(values[1]));
			}
			
			if (property.getKey().compareTo(Properties.MOTOR.name()) == 0) {
				String value = property.getValue();
				
				if(value.compareTo("1") == 0){
					Motor = true;
				}else{
					Motor = false;
				}
			}

			System.out.println(property);
		}

	}

	public void setUpForBuilder() {
		Body body = this.getObject().getPhysicsBody();
		body.setLinearDamping(10);
		body.setAngularDamping(10);
		this.getObject().setSensor();
	}

}
