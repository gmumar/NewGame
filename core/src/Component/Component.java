package Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import wrapper.BaseActor;
import JSONifier.JSONComponent;
import JSONifier.JSONComponentName;
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
	// private String componentName;
	private JSONComponentName jComponentName;
	private boolean Motor = false;
	private ArrayList<BaseActor> jointBodies = null;

	/*
	 * public Component(BaseActor obj, ComponentTypes type, String name) {
	 * object = obj; componentTypes = type; componentName = name; }
	 */

	public Component(BaseActor obj, ComponentTypes type, JSONComponentName name) {
		object = obj;
		componentTypes = type;
		jComponentName = name;
	}

	public ArrayList<BaseActor> getJointBodies() {
		return jointBodies;
	}

	public void setJointBodies(ArrayList<BaseActor> jointBodies) {
		this.jointBodies = jointBodies;
	}

	public void applyProperties(ComponentProperties propertiesIn,
			PropertyTypes type) {

		if (propertiesIn == null)
			return;

		if (type == PropertyTypes.ABSOLUTE || type == PropertyTypes.BOTH) {

			this.object
					.setRotation(Float.parseFloat(propertiesIn.getRotation()));
			this.Motor = propertiesIn.isMotor();
		}

		if (type == PropertyTypes.RELATIVE || type == PropertyTypes.BOTH) {

			this.setPosition(Float.parseFloat(propertiesIn.getPositionX()),
					Float.parseFloat(propertiesIn.getPositionY()));

		}

	}

	public void OLD_applyProperties(Map<String, String> propertiesIn,
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

		}

	}

	// public void setUpForBuilder(String name) {
	public void setUpForBuilder(JSONComponentName name, int partLevel) {
		// this.jComponentName = name;

		if (this.componentTypes == ComponentTypes.PART) {
			this.setComponentId(name.getComponentId());
			this.setMountId(name.getMountId());
			this.setPartLevel(partLevel);

			Body body = this.getObject().getPhysicsBody();
			body.setUserData(name);

			this.getObject().setSensor();
			// this.getObject().setBodyType(BodyType.KinematicBody);

			int mountId = 0;
			ArrayList<Vector2> mounts = this.getObject().getMounts();
			Iterator<Vector2> iter = mounts.iterator();
			while (iter.hasNext()) {
				Vector2 mount = iter.next();
				FixtureDef fixtureDef = ComponentBuilder.buildMount(mount, 1,
						true);
				Fixture fixture = body.createFixture(fixtureDef);
				JSONComponentName fixtureName = new JSONComponentName();
				fixtureName.setBaseName(name.getBaseName());
				fixtureName.setComponentId(name.getComponentId());
				// fixture.setUserData(name + Assembler.NAME_MOUNT_SPLIT
				// + mountId++);
				fixtureName.setMountId(Integer.toString(mountId++));

				fixture.setUserData(fixtureName);
			}
		} else if (this.componentTypes == ComponentTypes.JOINT) {

			ArrayList<BaseActor> bodies = this.getJointBodies();

			for (BaseActor body : bodies) {
				int mountId = 0;

				this.setComponentId(name.getComponentId());
				this.setPartLevel(partLevel);
				// TODO: finalize this
				body.setSensor();

				for (Vector2 mount : body.getMounts()) {
					JSONComponentName subBodyName = new JSONComponentName();
					FixtureDef fixtureDef = ComponentBuilder.buildMount(
							new Vector2(), 1, true);// <------------ This should
													// be using the mount here
					Fixture fixture = body.getPhysicsBody().createFixture(
							fixtureDef);

					subBodyName.setComponentId(name.getComponentId());
					subBodyName.setBaseName(name.getBaseName());
					subBodyName.setSubName(body.getjName().getSubName());
					subBodyName.setMountId(Integer.toString(mountId));
					subBodyName.setLevel(partLevel);

					fixture.setUserData(subBodyName);
					body.getPhysicsBody().setUserData(subBodyName);

					mountId++;
				}

			}
		}
	}

	// public JSONComponent toJSONComponent(String name) {
	public JSONComponent toJSONComponent(JSONComponentName name) {

		JSONComponent jComponent = new JSONComponent();
		// jComponent.setComponentName(name);
		jComponent.setjComponentName(name);

		boolean isMotor = false;
		ComponentTypes type = ComponentTypes.PART;
		ComponentProperties props = new ComponentProperties();

		if (name.getBaseName().compareTo(ComponentNames.AXLE) == 0) {
			isMotor = true;
		}

		props.setProperties(
				Float.toString(this.getObject().getRotation()
						* MathUtils.radiansToDegrees),
				Float.toString(this.getObject().getPosition().x),
				Float.toString(this.getObject().getPosition().y), isMotor, type);

		/*
		 * Map<String, String> prop = new HashMap<String, String>(); prop.put(
		 * Properties.ROTATION, Float.toString(this.getObject().getRotation()
		 * MathUtils.radiansToDegrees));// In radians
		 * prop.put(Properties.POSITION, Properties.makePostionString(this
		 * .getObject().getPosition().x, this.getObject().getPosition().y));
		 * 
		 * if (name.getBaseName().compareTo(ComponentNames.AXLE) == 0) {
		 * prop.put(Properties.MOTOR, "1"); } else if
		 * (name.getBaseName().compareTo(ComponentNames.SPRINGJOINT) == 0) {
		 * prop.put(Properties.TYPE, ComponentNames.SPRINGJOINT); }
		 */
		jComponent.setProperties(props);

		return jComponent;
	}

	// public JSONComponent toJSONComponent(String name, BaseActor body) {
	public JSONComponent toJSONComponent(JSONComponentName name, BaseActor body) {
		JSONComponent jComponent = new JSONComponent();
		jComponent.setjComponentName(name);

		/*
		 * HashMap<String, String> prop = new HashMap<String, String>();
		 * prop.put(Properties.ROTATION, Float.toString(body.getRotation() *
		 * MathUtils.radiansToDegrees));// In // radians
		 * prop.put(Properties.POSITION, body.getPosition().x + "," +
		 * body.getPosition().y); // "-10,5"
		 */

		ComponentProperties props = new ComponentProperties();

		props.setProperties(
				Float.toString(this.getObject().getRotation()
						* MathUtils.radiansToDegrees),
				Float.toString(this.getObject().getPosition().x),
				Float.toString(this.getObject().getPosition().y), false,
				ComponentTypes.PART);

		jComponent.setProperties(props);

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

	/*
	 * public String getComponentName() { return componentName; }
	 * 
	 * public void setComponentName(String componentName) { this.componentName =
	 * componentName; }
	 */

	public void destroyObject() {
		object.destroy();
	}

	public JSONComponentName getjComponentName() {
		return jComponentName;
	}

	public String getComponentName() {
		return jComponentName.getBaseName();
	}

	public void setjComponentName(JSONComponentName jComponentName) {
		this.jComponentName = jComponentName;
	}

	public void setBaseName(String baseName) {
		jComponentName.setBaseName(baseName);
	}

	public void setLevel(Integer level) {
		jComponentName.setLevel(level);
	}

	public Integer getLevel() {
		return jComponentName.getLevel();
	}

	public void setSubName(String subName) {
		jComponentName.setSubName(subName);
	}

	public void setComponentId(String componentId) {
		jComponentName.setComponentId(componentId);
	}

	public void setMountId(String mountId) {
		jComponentName.setMountId(mountId);
	}

	public void setPartLevel(Integer level) {
		jComponentName.setLevel(level);
		object.getjName().setLevel(level);

		if (jointBodies != null) {
			for (BaseActor body : jointBodies) {
				body.getjName().setLevel(level);
			}
		}
	}

	public void setOnlyPartLevel(Integer level) {
		jComponentName.setLevel(level);
		object.getjName().setLevel(level);
	}

	public Integer getPartLevel() {
		return object.getjName().getLevel();
	}

	public String getBaseName() {
		return jComponentName.getBaseName();
	}

	public String getSubName() {
		return jComponentName.getSubName();
	}

	public String getComponentId() {
		return jComponentName.getComponentId();
	}

	public String getMountId() {
		return jComponentName.getMountId();
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
				positionSetBodies.add(body.getPhysicsBody()); // Added this without thinkin
				body.setPosition(body.getPosition().x + f, body.getPosition().y
						+ g); // This line is the TODO
			}
		}

	}
	

	public void setRotation(float rot) {
		ArrayList<Body> positionSetBodies = new ArrayList<Body>();

		this.getObject().setRotation((this.getObject().getRotation()+rot)*MathUtils.radiansToDegrees);
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
				body.setRotation((body.getRotation()+rot)*MathUtils.radiansToDegrees);
			}
		}
	}

	public void setAbsoluteRotation(float rotRadians) {
		ArrayList<Body> positionSetBodies = new ArrayList<Body>();

		this.getObject().setRotation((rotRadians)*MathUtils.radiansToDegrees);
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
				body.setRotation((rotRadians)*MathUtils.radiansToDegrees);
			}
		}
	}

	public void setAbsolutePosition(float f, float g) {
		//this.getObject().setPosition(f, g);
		
		ArrayList<Body> positionSetBodies = new ArrayList<Body>();

		this.getObject().setPosition(f,g);
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
				positionSetBodies.add(body.getPhysicsBody());
				body.setPosition( f,  g); // This line is the TODO
			}
		}
	}

	/*public void setGroup(short group) {
		if (getJointBodies() == null) {
			this.getObject().setGroup(group);
		} else {
			Iterator<BaseActor> iter = getJointBodies().iterator();
			while (iter.hasNext()) {
				BaseActor body = iter.next();
				body.setGroup(group);
			}
		}

	}*/
	
	public void setFilter(short car, short ground){
		if (getJointBodies() == null) {
			this.getObject().setFilter(car, ground);
		} else {
			Iterator<BaseActor> iter = getJointBodies().iterator();
			while (iter.hasNext()) {
				BaseActor body = iter.next();
				body.setFilter(car, ground);
			}
		}
	}

	public void step() {
		if (getJointBodies() == null) {
			this.getObject().step();
		} else {
			/*
			 * Iterator<BaseActor> iter = getJointBodies().iterator(); while
			 * (iter.hasNext()) { BaseActor body = iter.next();
			 * body.draw(batch); }
			 */

			for (BaseActor body : getJointBodies()) {
				body.step();
			}
		}
	}

	public void draw(SpriteBatch batch) {
		if (getJointBodies() == null) {
			object.draw(batch);
		} else {
			/*
			 * Iterator<BaseActor> iter = getJointBodies().iterator(); while
			 * (iter.hasNext()) { BaseActor body = iter.next();
			 * body.draw(batch); }
			 */

			for (BaseActor body : getJointBodies()) {
				body.draw(batch);
			}
		}
	}

	public void setAlpha(float alpha) {
		if (getJointBodies() == null) {
			object.setAlpha(alpha);
		} else {
			for (BaseActor body : getJointBodies()) {
				body.setAlpha(alpha);
			}
		}
	}

	public void draw(SpriteBatch batch, String texture) {
		if (getJointBodies() == null) {
			object.draw(batch, texture);
		} else {
			/*
			 * Iterator<BaseActor> iter = getJointBodies().iterator(); while
			 * (iter.hasNext()) { BaseActor body = iter.next();
			 * body.draw(batch); }
			 */

			for (BaseActor body : getJointBodies()) {
				body.draw(batch, texture);
			}
		}
	}

	public void addTexture(String name, String texture) {
		this.getObject().addSprite(name, texture);
	}

	public Vector2 getPosition() {

		return getObject().getPosition();
	}

	public Vector2 getOrigin() {
		// TODO Auto-generated method stub
		return new Vector2(getObject().getOriginX(),getObject().getOriginY());
	}

}
