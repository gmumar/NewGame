package Assembly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import wrapper.BaseActor;
import wrapper.GamePreferences;
import Component.Component;
import Component.ComponentBuilder;
import Component.ComponentBuilder.ComponentNames;
import JSONifier.JSONComponent;
import JSONifier.JSONJoint;
import JSONifier.JSONParent;
import JSONifier.Properties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class Assembler {

	final public static String NAME_MOUNT_SPLIT = ":";
	final public static String NAME_ID_SPLIT = "_";

	final private short CAR = -2;

	public AssembledObject assembleObject(World world) {
		AssembledObject obj = new AssembledObject();
		Preferences prefs = Gdx.app.getPreferences(GamePreferences.CAR_PREF_STR);

		String inputString = prefs.getString(GamePreferences.CAR_MAP_STR, "Error");

		JSONParent source = new JSONParent();
		source = JSONParent.objectify(inputString);

		HashMap<String, Component> parts = extractComponents(source, world);

		// Read the JSONJoint array and build the obj
		ArrayList<JSONJoint> jcomponents = source.getJointList();
		Iterator<JSONJoint> JointIter = jcomponents.iterator();
		JSONJoint join;

		while (JointIter.hasNext()) {
			join = JointIter.next();

			String componentAName = parseName(join.mount1)[0];
			BaseActor bodyA = parts.get(componentAName).getObject();
			int componentAMountId = Integer.parseInt(parseName(join.mount1)[1]);

			String componentBName = parseName(join.mount2)[0];
			BaseActor bodyB = parts.get(componentBName).getObject();
			int componentBMountId = Integer.parseInt(parseName(join.mount2)[1]);

			RevoluteJointDef rJoint = new RevoluteJointDef();
			
			rJoint.initialize(bodyA.getPhysicsBody(), bodyB.getPhysicsBody(), bodyB.getMount(componentBMountId));
			rJoint.localAnchorA.set(bodyA.getMount(componentAMountId));
			rJoint.localAnchorB.set(bodyB.getMount(componentBMountId));
			rJoint.collideConnected = false;
			if(componentAName.contains(ComponentNames.tire.name()) || componentBName.contains(ComponentNames.tire.name())){
				System.out.println("motor found");
			}else{
				rJoint.lowerAngle = 0.01f;
				rJoint.enableLimit = true;
				rJoint.upperAngle = 0.01f;
			}

			world.createJoint(rJoint);

		}

		obj.setPartList(new ArrayList<Component>(parts.values()));
		obj.setBasePartbyIndex(0);
		return obj;
	}

	private HashMap<String, Component> extractComponents(JSONParent source,
			World world) {
		HashMap<String, Component> ret = new HashMap<String, Component>();
		ArrayList<JSONComponent> jcomponents = source.getComponentList();

		Iterator<JSONComponent> iter = jcomponents.iterator();
		JSONComponent sourceComponent;
		Component component = null;
		String componentName;

		while (iter.hasNext()) {
			sourceComponent = iter.next();
			componentName = getComponentName(sourceComponent.getComponentName());
			component = ComponentBuilder.buildComponent(componentName, world);
			if(sourceComponent.getProperties() != null){
				component.applyProperties(sourceComponent.getProperties());
			}
			
			component.getObject().setGroup(CAR);
			ret.put(sourceComponent.getComponentName(), component);
		}

		return ret;
	}

	private String[] parseName(String name) {
		// e.g. bar3_0 , 1
		return name.split(NAME_MOUNT_SPLIT);
	}

	private String getComponentName(String name) {
		// e.g. bar3
		return name.split(NAME_ID_SPLIT)[0];
	}

	public String makeSomething() {

		JSONParent car = new JSONParent();

		// component list
		ArrayList<JSONComponent> parts = new ArrayList<JSONComponent>();
		JSONComponent part1 = new JSONComponent();
		part1.setComponentName(ComponentNames.bar3.name() + NAME_ID_SPLIT + "0");
		parts.add(part1);

		JSONComponent part2 = new JSONComponent();
		part2.setComponentName(ComponentNames.bar3.name() + NAME_ID_SPLIT + "1");
		HashMap<String, String> prop2 = new HashMap<String, String>();
		prop2.put(Properties.ROTATION.name(), "90");
		prop2.put(Properties.POSITION.name(), "-10,5");
		part2.setProperties(prop2);
		parts.add(part2);

		JSONComponent part3 = new JSONComponent();
		part3.setComponentName(ComponentNames.bar3.name() + NAME_ID_SPLIT + "2");
		HashMap<String, String> prop3 = new HashMap<String, String>();
		prop3.put(Properties.ROTATION.name(), "90");
		prop3.put(Properties.POSITION.name(), "10,5");
		part3.setProperties(prop3);
		parts.add(part3);

		JSONComponent part4 = new JSONComponent();
		part4.setComponentName(ComponentNames.tire.name() + NAME_ID_SPLIT + "0");
		HashMap<String, String> prop4 = new HashMap<String, String>();
		prop4.put(Properties.MOTOR.name(), "1");
		part4.setProperties(prop4);
		parts.add(part4);

		JSONComponent part5 = new JSONComponent();
		part5.setComponentName(ComponentNames.tire.name() + NAME_ID_SPLIT + "1");
		HashMap<String, String> prop5 = new HashMap<String, String>();
		prop5.put(Properties.MOTOR.name(), "1");
		part5.setProperties(prop5);
		parts.add(part5);

		JSONComponent part6 = new JSONComponent();
		part6.setComponentName(ComponentNames.tire.name() + NAME_ID_SPLIT + "2");
		HashMap<String, String> prop6 = new HashMap<String, String>();
		prop6.put(Properties.MOTOR.name(), "1");
		part6.setProperties(prop6);
		
		parts.add(part6);
		car.setComponentList(parts);

		// joint list
		ArrayList<JSONJoint> joints = new ArrayList<JSONJoint>();
		JSONJoint joint1 = new JSONJoint();
		joint1.setMount1(ComponentNames.bar3.name() + NAME_ID_SPLIT + "0"
				+ NAME_MOUNT_SPLIT + "0");
		joint1.setMount2(ComponentNames.bar3.name() + NAME_ID_SPLIT + "1"
				+ NAME_MOUNT_SPLIT + "0");
		joints.add(joint1);

		JSONJoint joint2 = new JSONJoint();
		joint2.setMount1(ComponentNames.bar3.name() + NAME_ID_SPLIT + "1"
				+ NAME_MOUNT_SPLIT + "2");
		joint2.setMount2(ComponentNames.bar3.name() + NAME_ID_SPLIT + "2"
				+ NAME_MOUNT_SPLIT + "0");
		joints.add(joint2);

		JSONJoint joint3 = new JSONJoint();
		joint3.setMount1(ComponentNames.bar3.name() + NAME_ID_SPLIT + "2"
				+ NAME_MOUNT_SPLIT + "2");
		joint3.setMount2(ComponentNames.bar3.name() + NAME_ID_SPLIT + "0"
				+ NAME_MOUNT_SPLIT + "2");
		joints.add(joint3);
		
		JSONJoint joint4 = new JSONJoint();
		joint4.setMount1(ComponentNames.bar3.name() + NAME_ID_SPLIT + "2"
				+ NAME_MOUNT_SPLIT + "2");
		joint4.setMount2(ComponentNames.tire.name() + NAME_ID_SPLIT + "0"
				+ NAME_MOUNT_SPLIT + "0");
		joints.add(joint4);

		JSONJoint joint5 = new JSONJoint();
		joint5.setMount1(ComponentNames.bar3.name() + NAME_ID_SPLIT + "2"
				+ NAME_MOUNT_SPLIT + "0");
		joint5.setMount2(ComponentNames.tire.name() + NAME_ID_SPLIT + "1"
				+ NAME_MOUNT_SPLIT + "0");
		joints.add(joint5);

		JSONJoint joint6 = new JSONJoint();
		joint6.setMount1(ComponentNames.bar3.name() + NAME_ID_SPLIT + "0"
				+ NAME_MOUNT_SPLIT + "0");
		joint6.setMount2(ComponentNames.tire.name() + NAME_ID_SPLIT + "2"
				+ NAME_MOUNT_SPLIT + "0");
		joints.add(joint6);

		car.setJointList(joints);

		return car.jsonify();
	}

}
