package Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import wrapper.BaseActor;
import Assembly.Assembler;
import JSONifier.JSONComponent;
import JSONifier.Properties;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Component {

	public enum ComponentTypes {
		PART, JOINT
	}

	public enum PropertyTypes {
		RELATIVE, ABSOLUTE, BOTH
	}

	private BaseActor object;
	private ComponentTypes componentTypes;
	private String componentName;
	private boolean Motor = false;
	private ArrayList<BaseActor> jointBodies = null;

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

	public void applyProperties(HashMap<String, String> propertiesIn,
			PropertyTypes type) {
		if (propertiesIn.isEmpty())
			return;

		Set<Entry<String, String>> properties = propertiesIn.entrySet();

		Iterator<Entry<String, String>> iter = properties.iterator();
		while (iter.hasNext()) {
			Entry<String, String> property = iter.next();

			if (type == PropertyTypes.ABSOLUTE || type == PropertyTypes.BOTH) {
				if (property.getKey().compareTo(Properties.ROTATION) == 0) {
					String value = property.getValue();
					this.object.setRotation(Float.parseFloat(value));
				}

				if (property.getKey().compareTo(Properties.MOTOR) == 0) {
					String value = property.getValue();

					if (value.compareTo("1") == 0) {
						Motor = true;
					} else {
						Motor = false;
					}
				}
			} 
			
			if (type == PropertyTypes.RELATIVE || type == PropertyTypes.BOTH) {

				if (property.getKey().compareTo(Properties.POSITION) == 0) {
					String[] values = property.getValue().split(",");
					this.setPosition(Float.parseFloat(values[0]),
							Float.parseFloat(values[1]));
				}
			}

			System.out.println(this + " " + property);
		}

	}

	public void setUpForBuilder(String name) {
		if (this.componentTypes == ComponentTypes.PART) {
			Body body = this.getObject().getPhysicsBody();
			body.setUserData(name);

			this.getObject().setSensor();
			// this.getObject().setBodyType(BodyType.KinematicBody);

			int mountId = 0;
			ArrayList<Vector2> mounts = this.getObject().getMounts();
			Iterator<Vector2> iter = mounts.iterator();
			while (iter.hasNext()) {
				Vector2 mount = iter.next();
				FixtureDef fixtureDef = ComponentBuilder
						.buildMount(mount, true);
				Fixture fixture = body.createFixture(fixtureDef);
				fixture.setUserData(name + Assembler.NAME_MOUNT_SPLIT
						+ mountId++);
			}
		} else if (this.componentTypes == ComponentTypes.JOINT) {
			int mountId = 0;
			ArrayList<BaseActor> bodies = this.getJointBodies();
			Iterator<BaseActor> iter = bodies.iterator();
			while (iter.hasNext()) {
				BaseActor body = iter.next();
				// TODO: finalize this
				body.setSensor();
				// body.setBodyType(BodyType.KinematicBody);
				FixtureDef fixtureDef = ComponentBuilder.buildMount(
						new Vector2(0, 0), true);
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
				Properties.ROTATION,
				Float.toString(this.getObject().getRotation()
						* MathUtils.radiansToDegrees));// In radians
		prop.put(Properties.POSITION, this.getObject().getPosition().x
				+ "," + this.getObject().getPosition().y); // "-10,5"

		if (name.contains(ComponentNames.TIRE)) {
			prop.put(Properties.MOTOR, "1");
		} else if (name.contains(ComponentNames.SPRINGJOINT)) {
			prop.put(Properties.TYPE,
					ComponentNames.SPRINGJOINT);
		}
		jComponent.setProperties(prop);

		return jComponent;
	}

	public JSONComponent toJSONComponent(String name, BaseActor body) {
		JSONComponent jComponent = new JSONComponent();
		jComponent.setComponentName(name);

		HashMap<String, String> prop = new HashMap<String, String>();
		prop.put(Properties.ROTATION,
				Float.toString(body.getRotation() * MathUtils.radiansToDegrees));// In
																					// radians
		prop.put(Properties.POSITION,
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

	public void setPosition(float f, float g) {

		ArrayList<Body> positionSetBodies = new ArrayList<Body>();

		this.getObject().setPosition(this.getObject().getPosition().x + f,
				this.getObject().getPosition().y + g);
		positionSetBodies.add(this.getObject().getPhysicsBody());
		if (getJointBodies() == null) {
			;
		} else {
			Iterator<BaseActor> iter = getJointBodies().iterator();

			while (iter.hasNext()) {
				BaseActor body = iter.next();
				if (positionSetBodies.contains(body.getPhysicsBody())) {
					continue;
				}
				body.setPosition(body.getPosition().x + f, body.getPosition().y
						+ g); // This line is the TODO
				System.out.println(body + " " + body.getName()
						+ " Setting position " + body.getPosition());
			}
		}

	}

	public void setAbsolutePosition(float f, float g) {
		this.getObject().setPosition(f, g);
	}

	public void setGroup(short group) {
		if (getJointBodies() == null) {
			this.getObject().setGroup(group);
		} else {
			Iterator<BaseActor> iter = getJointBodies().iterator();
			while (iter.hasNext()) {
				BaseActor body = iter.next();
				body.setGroup(group);
			}
		}

	}

	public void draw(SpriteBatch batch) {
		if (getJointBodies() == null) {
			this.getObject().draw(batch);
		} else {
			Iterator<BaseActor> iter = getJointBodies().iterator();
			while (iter.hasNext()) {
				BaseActor body = iter.next();
				body.draw(batch);
			}
		}
	}

}
