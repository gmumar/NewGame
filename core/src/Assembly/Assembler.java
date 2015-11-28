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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

public class Assembler {

	final public static String NAME_MOUNT_SPLIT = ":";
	final public static String NAME_ID_SPLIT = "_";
	final public static String NAME_SUBNAME_SPLIT = "=";

	final private short CAR = -2;

	public AssembledObject assembleObject(World world) {
		AssembledObject obj = new AssembledObject();
		Preferences prefs = Gdx.app
				.getPreferences(GamePreferences.CAR_PREF_STR);

		String inputString = 
				prefs.getString(GamePreferences.CAR_MAP_STR,"Error");
		// "{jointList:[{mount1:springJoint=upper_1:0,mount2:tire_1:0},{mount1:bar3_0:0,mount2:springJoint=lower_1:0},{mount1:springJoint=upper_0:0,mount2:tire_0:0},{mount1:bar3_0:2,mount2:springJoint=lower_0:0}],componentList:[{componentName:bar3_0,properties:{ROTATION:0.0,POSITION:\"0.0,0.0\"}},{componentName:springJoint_0,properties:{ROTATION:1.4883224,POSITION:\"1.313098,-1.0663831\"}},{componentName:tire_0,properties:{MOTOR:1,ROTATION:0.0,POSITION:\"1.25,-1.1499996\"}},{componentName:springJoint_1,properties:{ROTATION:-0.33204922,POSITION:\"-1.3914706,-1.3713517\"}},{componentName:tire_1,properties:{MOTOR:1,ROTATION:0.0,POSITION:\"-1.3499994,-1.3000002\"}}]}";
		//

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
			// System.out.println(componentAName);
			BaseActor bodyA = parts.get(componentAName).getObject();
			int componentAMountId = getMountId(join.mount1);

			String componentBName = parseName(join.mount2)[0];
			// System.out.println(componentBName);
			BaseActor bodyB = parts.get(componentBName).getObject();
			int componentBMountId = getMountId(join.mount2);

			{
				RevoluteJointDef rJoint = new RevoluteJointDef();

				rJoint.initialize(bodyA.getPhysicsBody(),
						bodyB.getPhysicsBody(),
						bodyB.getMount(componentBMountId));
				rJoint.localAnchorA.set(bodyA.getMount(componentAMountId));
				rJoint.localAnchorB.set(bodyB.getMount(componentBMountId));
				rJoint.collideConnected = false;
				//rJoint.lowerAngle = 0.00f;
				//rJoint.upperAngle = 0.4f;
				rJoint.enableLimit = true;
				world.createJoint(rJoint);
				
				
			}

		}

		obj.setPartList(new ArrayList<Component>(parts.values()));
		obj.setBasePartbyIndex(0);
		return obj;
	}

	private boolean contains(ArrayList<JSONJoint> jointExclusionList,
			JSONJoint joinIn) {

		if (jointExclusionList.isEmpty())
			return false;

		Iterator<JSONJoint> JointIter = jointExclusionList.iterator();
		JSONJoint join;

		while (JointIter.hasNext()) {
			join = JointIter.next();

			if (joinIn.getMount1().compareTo(join.getMount1()) == 0
					&& joinIn.getMount2().compareTo(join.getMount2()) == 0
					&& joinIn.properties != null
					&& joinIn.properties.size() == join.properties.size()) {
				return true;
			}

		}
		return false;
	}

	private HashMap<String, Component> extractComponents(JSONParent source,
			World world) {
		HashMap<String, Component> ret = new HashMap<String, Component>();
		ArrayList<JSONComponent> jcomponents = source.getComponentList();

		Iterator<JSONComponent> iter = jcomponents.iterator();
		JSONComponent sourceComponent;
		Component component = null;
		ArrayList<Component> componentList = null;
		String componentName;

		while (iter.hasNext()) {
			componentList = null;
			component = null;
			sourceComponent = iter.next();
			componentName = getComponentName(sourceComponent.getComponentName());

			System.out.println("Extracting: " + componentName);

			if (componentName.contains(ComponentNames.springJoint.name())) {
				componentList = ComponentBuilder.buildJointComponent(
						componentName, world);

				String[] nameList = sourceComponent.getComponentName().split(
						NAME_ID_SPLIT);
				String jointComponentName = nameList[0];
				String jointComponentId = nameList[1];

				Component part1 = componentList.get(0);
				Component part2 = componentList.get(1);

				part1.setGroup(CAR);
				part2.setGroup(CAR);
				part1.applyProperties(sourceComponent.getProperties());
				// part2.applyProperties(sourceComponent.getProperties());

				ret.put(jointComponentName + NAME_SUBNAME_SPLIT
						+ ComponentSubNames.upper.name() + NAME_ID_SPLIT
						+ jointComponentId, part1);
				ret.put(jointComponentName + NAME_SUBNAME_SPLIT
						+ ComponentSubNames.lower.name() + NAME_ID_SPLIT
						+ jointComponentId, part2);

			} else {

				component = ComponentBuilder.buildComponent(componentName,
						world);

				if (sourceComponent.getProperties() != null) {
					component.applyProperties(sourceComponent.getProperties());
				}
				component.setGroup(CAR);
				ret.put(sourceComponent.getComponentName(), component);
			}

		}

		return ret;
	}

	private String getId(String name) {
		return name.split(NAME_ID_SPLIT)[1];
	}

	private String getSubname(String name) {
		String subpart = name.split(NAME_SUBNAME_SPLIT)[1];
		return subpart.split(NAME_ID_SPLIT)[0];
	}

	private int getMountId(String name) {
		return Integer.parseInt(parseName(name)[1]);
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
