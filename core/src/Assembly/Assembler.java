package Assembly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import wrapper.BaseActor;
import wrapper.GamePreferences;
import Component.Component;
import Component.ComponentBuilder;
import Component.ComponentBuilder.ComponentNames;
import Component.ComponentBuilder.ComponentSubNames;
import JSONifier.JSONComponent;
import JSONifier.JSONJoint;
import JSONifier.JSONParent;
import JSONifier.JointTypes;
import JSONifier.Properties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class Assembler {

	final public static String NAME_MOUNT_SPLIT = ":";
	final public static String NAME_ID_SPLIT = "_";
	final public static String NAME_SUBNAME_SPLIT = "=";

	final private short CAR = -2;

	public AssembledObject assembleObject(World world) {
		AssembledObject obj = new AssembledObject();
		Preferences prefs = Gdx.app
				.getPreferences(GamePreferences.CAR_PREF_STR);

		String inputString = prefs.getString(GamePreferences.CAR_MAP_STR,
				"Error");

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

			System.out.println(componentBName);
			System.out.println(componentBMountId);
			HashMap<String, String> props = join.getProperties();

			String jointType = JointTypes.REVOLUTE.name();
			if (props != null) {
				jointType = getJointType(props);
			}

			if (jointType.compareTo(JointTypes.REVOLUTE.name()) == 0) {

				RevoluteJointDef rJoint = new RevoluteJointDef();

				rJoint.initialize(bodyA.getPhysicsBody(),
						bodyB.getPhysicsBody(),
						bodyB.getMount(componentBMountId));
				rJoint.localAnchorA.set(bodyA.getMount(componentAMountId));
				rJoint.localAnchorB.set(bodyB.getMount(componentBMountId));
				rJoint.collideConnected = false;
				if (componentAName.contains(ComponentNames.tire.name())
						|| componentBName.contains(ComponentNames.tire.name())) {
					;
				} else {
					rJoint.lowerAngle = 0.01f;
					rJoint.enableLimit = true;
					rJoint.upperAngle = 0.01f;
				}

				world.createJoint(rJoint);
			} else if (jointType.compareTo(JointTypes.SPRING.name()) == 0) {

				RevoluteJointDef rJoint = new RevoluteJointDef();

				rJoint.initialize(bodyA.getPhysicsBody(),
						bodyB.getPhysicsBody(),
						bodyB.getMount(componentBMountId));
				rJoint.localAnchorA.set(bodyA.getMount(componentAMountId));
				rJoint.localAnchorB.set(bodyB.getMount(componentBMountId));
				rJoint.collideConnected = false;
				if (componentAName.contains(ComponentNames.tire.name())
						|| componentBName.contains(ComponentNames.tire.name())) {
					;
				} else {
					rJoint.lowerAngle = 0.01f;
					rJoint.enableLimit = true;
					rJoint.upperAngle = 0.01f;
				}

				world.createJoint(rJoint);
			}

		}

		obj.setPartList(new ArrayList<Component>(parts.values()));
		obj.setBasePartbyIndex(0);
		return obj;
	}

	private String getJointType(HashMap<String, String> props) {
		if (props.containsKey(Properties.TYPE)) {
			return props.get(Properties.TYPE);
		}
		return JointTypes.REVOLUTE.name();
	}

	private HashMap<String, Component> extractComponents(JSONParent source,
			World world) {
		HashMap<String, Component> ret = new HashMap<String, Component>();
		ArrayList<JSONComponent> jcomponents = source.getComponentList();
		String subnameIndex;
		String[] componentNameList = new String[2];

		Iterator<JSONComponent> iter = jcomponents.iterator();
		JSONComponent sourceComponent;
		Component component = null;
		String componentName;

		while (iter.hasNext()) {
			sourceComponent = iter.next();
			componentName = getComponentName(sourceComponent.getComponentName());
			
			if (componentName.contains(NAME_SUBNAME_SPLIT)) {
				componentNameList = componentName.split(NAME_SUBNAME_SPLIT);
				componentName = componentNameList[0];
				subnameIndex = componentNameList[1];
				if(subnameIndex.compareTo(ComponentSubNames.upper.name())==0){
					component = ComponentBuilder.buildComponent(componentName, world);
				} 
			}else{
				component = ComponentBuilder.buildComponent(componentName, world);
			}
			
			if (sourceComponent.getProperties() != null) {
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

}
