package Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import wrapper.BaseActor;
import Assembly.Assembler;
import Component.ComponentBuilder.ComponentNames;
import JSONifier.JSONComponent;
import JSONifier.Properties;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Component {

	public enum ComponentTypes {
		PART, JOINT
	}

	BaseActor object;
	ComponentTypes componentTypes;
	String componentName;
	boolean Motor = false;
	ArrayList<BaseActor> jointBodies = null;

	public Component(BaseActor obj, ComponentTypes type, String name) {
		object = obj;
		componentTypes = type;
		componentName = name;
	}

	public ArrayList<BaseActor> getJointBodies() {
		return jointBodies;
	}

	public void setJointBodies(ArrayList<BaseActor> jointBodies) {
		this.jointBodies = jointBodies;
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
				this.object.setPosition(Float.parseFloat(values[0]),
						Float.parseFloat(values[1]));
			}

			if (property.getKey().compareTo(Properties.MOTOR.name()) == 0) {
				String value = property.getValue();

				if (value.compareTo("1") == 0) {
					Motor = true;
				} else {
					Motor = false;
				}
			}

			// System.out.println(property);
		}

	}

	public void setUpForBuilder(String name) {
		if (this.componentTypes == ComponentTypes.PART) {
			System.out.println("setting up part");
			Body body = this.getObject().getPhysicsBody();
			body.setUserData(name);

			this.getObject().setSensor();
			//this.getObject().setBodyType(BodyType.KinematicBody);

			int mountId = 0;
			ArrayList<Vector2> mounts = this.getObject().getMounts();
			Iterator<Vector2> iter = mounts.iterator();
			while (iter.hasNext()) {
				Vector2 mount = iter.next();
				FixtureDef fixtureDef = ComponentBuilder.buildMount(mount);
				Fixture fixture = body.createFixture(fixtureDef);
				fixture.setUserData(name + Assembler.NAME_MOUNT_SPLIT
						+ mountId++);
			}
		} else if (this.componentTypes == ComponentTypes.JOINT) {
			System.out.println("setting up joint");
			int mountId = 0;
			ArrayList<BaseActor> bodies = this.getJointBodies();
			Iterator<BaseActor> iter = bodies.iterator();
			while (iter.hasNext()) {
				BaseActor body = iter.next();
				// TODO: finalize this
				body.setSensor();
				//body.setBodyType(BodyType.KinematicBody);
				FixtureDef fixtureDef = ComponentBuilder
						.buildMount(new Vector2(0, 0));
				Fixture fixture = body.getPhysicsBody().createFixture(
						fixtureDef);
				fixture.setUserData(body.getPhysicsBody().getUserData() + name
						+ Assembler.NAME_MOUNT_SPLIT + mountId);
				body.getPhysicsBody().setUserData(
						body.getPhysicsBody().getUserData() + name);
			}
		}
	}

	public JSONComponent toJSONComponent(String name) {
		JSONComponent jComponent = new JSONComponent();
		jComponent.setComponentName(name);

		HashMap<String, String> prop = new HashMap<String, String>();
		prop.put(
				Properties.ROTATION.name(),
				Float.toString(this.getObject().getRotation()
						* MathUtils.radiansToDegrees));// In radians
		prop.put(Properties.POSITION.name(), this.getObject().getPosition().x
				+ "," + this.getObject().getPosition().y); // "-10,5"

		if (name.contains(ComponentNames.tire.name())) {
			prop.put(Properties.MOTOR.name(), "1");
		} else if (name.contains(ComponentNames.springJoint.name())) {
			prop.put(Properties.TYPE.name(), ComponentNames.springJoint.name());
		}
		jComponent.setProperties(prop);

		return jComponent;
	}

	public JSONComponent toJSONComponent(String name, BaseActor body) {
		JSONComponent jComponent = new JSONComponent();
		jComponent.setComponentName(name);

		HashMap<String, String> prop = new HashMap<String, String>();
		prop.put(Properties.ROTATION.name(),
				Float.toString(body.getRotation() * MathUtils.radiansToDegrees));// In
																					// radians
		prop.put(Properties.POSITION.name(),
				body.getPosition().x + "," + body.getPosition().y); // "-10,5"

		jComponent.setProperties(prop);

		return jComponent;
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

}
