package Assembly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import wrapper.BaseActor;
import wrapper.GamePreferences;
import wrapper.GameState;
import wrapper.Globals;
import Component.Component;
import Component.Component.PropertyTypes;
import Component.ComponentBuilder;
import Component.ComponentBuilder.ComponentNames;
import Component.ComponentBuilder.ComponentSubNames;
import GroundWorks.GroundUnitDescriptor;
import JSONifier.JSONComponent;
import JSONifier.JSONJoint;
import JSONifier.JSONParent;
import JSONifier.JSONTrack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

public class Assembler {

	final public static String NAME_MOUNT_SPLIT = ":";
	final public static String NAME_ID_SPLIT = "-";
	final public static String NAME_SUBNAME_SPLIT = "=";

	final private static short CAR = -2;

	public AssembledObject assembleObject(GameState gameState) {
		AssembledObject obj = new AssembledObject();
		Preferences prefs = Gdx.app
				.getPreferences(GamePreferences.CAR_PREF_STR);

		String inputString = prefs
				.getString(
						GamePreferences.CAR_MAP_STR,
						"{jointList:[{mount1:springJoint=upper_1:0,mount2:tire_1:0},{mount1:bar3_0:0,mount2:springJoint=lower_1:0},{mount1:springJoint=upper_0:0,mount2:tire_0:0},{mount1:bar3_0:2,mount2:springJoint=lower_0:0}],componentList:[{componentName:bar3_0,properties:{ROTATION:0.0,POSITION:\"0.0,0.0\"}},{componentName:springJoint_0,properties:{ROTATION:1.4883224,POSITION:\"1.313098,-1.0663831\"}},{componentName:tire_0,properties:{MOTOR:1,ROTATION:0.0,POSITION:\"1.25,-1.1499996\"}},{componentName:springJoint_1,properties:{ROTATION:-0.33204922,POSITION:\"-1.3914706,-1.3713517\"}},{componentName:tire_1,properties:{MOTOR:1,ROTATION:0.0,POSITION:\"-1.3499994,-1.3000002\"}}]}");//

		JSONParent source = new JSONParent();
		source = JSONParent.objectify(inputString);
		
		HashMap<String, Component> parts = extractComponents(source, gameState);
		// Read the JSONJoint array and build the obj
		ArrayList<JSONJoint> jcomponents = source.getJointList();
		Iterator<JSONJoint> JointIter = jcomponents.iterator();
		JSONJoint join;

		while (JointIter.hasNext()) {
			join = JointIter.next();

			String componentAName = Globals.parseName(join.mount1)[0];
			// System.out.println(componentAName);
			BaseActor bodyA = parts.get(componentAName).getObject();
			int componentAMountId = Globals.getMountId(join.mount1);

			String componentBName = Globals.parseName(join.mount2)[0];
			// System.out.println(componentBName);
			BaseActor bodyB = parts.get(componentBName).getObject();
			int componentBMountId = Globals.getMountId(join.mount2);

			{
				/*RevoluteJointDef rJoint = new RevoluteJointDef();

				rJoint.initialize(bodyA.getPhysicsBody(),
						bodyB.getPhysicsBody(),
						bodyB.getMount(componentBMountId));
				rJoint.localAnchorA.set(bodyA.getMount(componentAMountId));
				rJoint.localAnchorB.set(bodyB.getMount(componentBMountId));
				rJoint.collideConnected = false;
				rJoint.enableLimit = true;
				world.createJoint(rJoint);*/

				
				WeldJointDef wJoint = new WeldJointDef();

				wJoint.initialize(bodyA.getPhysicsBody(),
						bodyB.getPhysicsBody(),
						bodyB.getMount(componentBMountId));
				wJoint.localAnchorA.set(bodyA.getMount(componentAMountId));
				wJoint.localAnchorB.set(bodyB.getMount(componentBMountId));
				wJoint.collideConnected = false;
				gameState.getWorld().createJoint(wJoint);

			}

		}

		obj.setPartList(new ArrayList<Component>(parts.values()));
		obj.setLifeBasePart();
		return obj;
	}

	private HashMap<String, Component> extractComponents(JSONParent source,
			GameState gameState) {
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
			componentName = Globals.getComponentName(sourceComponent
					.getComponentName());

			System.out.println("Extracting: " + componentName);
			
			if (componentName.contains(ComponentNames._SPRINGJOINT_.name()) ) {
				componentList = ComponentBuilder.buildJointComponent(
						componentName, gameState);

				String[] nameList = sourceComponent.getComponentName().split(
						NAME_ID_SPLIT);
				String jointComponentName = nameList[0];
				String jointComponentId = nameList[1];
				
				Iterator<Component> it = componentList.iterator();
				while(it.hasNext()){
					Component localComponent = it.next();
					localComponent.setGroup(CAR);
					//localComponent.applyProperties(sourceComponent.getProperties());
					
				}
				
				componentList.get(0).applyProperties(sourceComponent.getProperties(),PropertyTypes.BOTH);
				componentList.get(1).applyProperties(sourceComponent.getProperties(),PropertyTypes.ABSOLUTE);
				
				componentList.get(0).setComponentName(jointComponentName + NAME_SUBNAME_SPLIT
						+ ComponentSubNames._UPPER_.name() + NAME_ID_SPLIT
						+ jointComponentId);
				
				componentList.get(1).setComponentName(jointComponentName + NAME_SUBNAME_SPLIT
						+ ComponentSubNames._LOWER_.name() + NAME_ID_SPLIT
						+ jointComponentId);
				
				ret.put(componentList.get(0).getComponentName(), componentList.get(0));
				ret.put(jointComponentName + NAME_SUBNAME_SPLIT
						+ ComponentSubNames._LOWER_.name() + NAME_ID_SPLIT
						+ jointComponentId, componentList.get(1));

			} else {

				component = ComponentBuilder.buildComponent(componentName,
						gameState);

				if (sourceComponent.getProperties() != null) {
					component.applyProperties(sourceComponent.getProperties(), PropertyTypes.BOTH);
				}
				component.setGroup(CAR);
				component.setComponentName(sourceComponent.getComponentName());
				ret.put(sourceComponent.getComponentName(), component);
			}

		}

		return ret;
	}

	public ArrayList<GroundUnitDescriptor> assembleTrack(String mapString, Vector2 offset) {
		JSONTrack jsonTrack = JSONTrack.objectify(mapString);
		ArrayList<Vector2> mapPoints = jsonTrack.getPoints();
		
		//System.out.println(mapPoints.size());
		
		ArrayList <GroundUnitDescriptor> retList = new ArrayList<GroundUnitDescriptor>();
		
		Iterator<Vector2> iter = mapPoints.iterator();
		Vector2 lastPoint, point = null;
		boolean first = true;
		
		while(iter.hasNext()){
			if(first){
				point = iter.next();
				point.x += offset.x;
				point.y += offset.y;
				first = !first;
				continue;
			}
			lastPoint = point;
			point = iter.next();
			point.x += offset.x;
			point.y += offset.y;
			GroundUnitDescriptor gud = new GroundUnitDescriptor(lastPoint, point, false);//, "texture.png");
			retList.add(gud);
			
		}
		
		return retList;
	}

}
