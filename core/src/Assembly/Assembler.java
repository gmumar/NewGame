package Assembly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import wrapper.BaseActor;
import wrapper.CameraManager;
import wrapper.GamePreferences;
import wrapper.GamePhysicalState;
import wrapper.Globals;
import Component.Component;
import Component.Component.PropertyTypes;
import Component.ComponentBuilder;
import Component.ComponentNames;
import Component.ComponentSubNames;
import GroundWorks.GroundUnitDescriptor;
import JSONifier.JSONCar;
import JSONifier.JSONComponent;
import JSONifier.JSONComponentName;
import JSONifier.JSONJoint;
import JSONifier.JSONTrack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.gudesigns.climber.GameLoader;

public class Assembler {

	//final public static String NAME_MOUNT_SPLIT = ":";
	//final public static String NAME_ID_SPLIT = "-";
	//final public static String NAME_SUBNAME_SPLIT = "=";

	final private static short CAR = -2;

	public static AssembledObject assembleObject(GamePhysicalState gameState) {
		Integer jointType = 0;
		
		AssembledObject obj = new AssembledObject();
		Preferences prefs = Gdx.app
				.getPreferences(GamePreferences.CAR_PREF_STR);

		String inputString = prefs
				.getString(
						GamePreferences.CAR_MAP_STR,
						"{jointList:[{mount1:springJoint=upper_1:0,mount2:tire_1:0},{mount1:bar3_0:0,mount2:springJoint=lower_1:0},{mount1:springJoint=upper_0:0,mount2:tire_0:0},{mount1:bar3_0:2,mount2:springJoint=lower_0:0}],componentList:[{componentName:bar3_0,properties:{ROTATION:0.0,POSITION:\"0.0,0.0\"}},{componentName:springJoint_0,properties:{ROTATION:1.4883224,POSITION:\"1.313098,-1.0663831\"}},{componentName:tire_0,properties:{MOTOR:1,ROTATION:0.0,POSITION:\"1.25,-1.1499996\"}},{componentName:springJoint_1,properties:{ROTATION:-0.33204922,POSITION:\"-1.3914706,-1.3713517\"}},{componentName:tire_1,properties:{MOTOR:1,ROTATION:0.0,POSITION:\"-1.3499994,-1.3000002\"}}]}");//

		JSONCar source = new JSONCar();
		source = JSONCar.objectify(inputString);

		//HashMap<String, Component> parts = extractComponents(source, gameState);
		HashMap<String, Component> parts = extractComponents(source, gameState);
		//System.out.println("Assembler :" + parts);
		// Read the JSONJoint array and build the obj
		ArrayList<JSONJoint> jcomponents = source.getJointList();
		Iterator<JSONJoint> JointIter = jcomponents.iterator();
		JSONJoint join;
		
		HashMap<String, Integer> jointTypeList = source.getJointTypeList();
		System.out.println("Assembler : jointType" + jointTypeList);

		while (JointIter.hasNext()) {
			join = JointIter.next();

			/*String componentAName = Globals.parseName(join.m1)[0];
			// System.out.println(componentAName);
			BaseActor bodyA = parts.get(componentAName).getObject();
			int componentAMountId = Globals.getMountId(join.m1);*/
			
			String componentAName = join.getMount1().getId();
			System.out.println("Assembler : componentA " + join.getMount1().getMountedId());
			
			BaseActor bodyA = parts.get(componentAName).
							getObject();
			int componentAMountId = Integer.parseInt(join.getMount1().getMountId());

			/*String componentBName = Globals.parseName(join.m2)[0];
			// System.out.println(componentBName);
			BaseActor bodyB = parts.get(componentBName).getObject();
			int componentBMountId = Globals.getMountId(join.m2);*/
			
			String componentBName = join.getMount2().getId();
			System.out.println("Assembler : componentB " + join.getMount2().getMountedId());
			BaseActor bodyB = parts.get(componentBName).getObject();
			int componentBMountId = Integer.parseInt(join.getMount2().getMountId());
			
			jointType = jointTypeList.get(join.getMount1().getMountedId());

			if(jointType == 1){
				
				  RevoluteJointDef rJoint = new RevoluteJointDef();
				  
				  rJoint.initialize(bodyA.getPhysicsBody(),
				  bodyB.getPhysicsBody(), bodyB.getMount(componentBMountId));
				  rJoint.localAnchorA.set(bodyA.getMount(componentAMountId));
				  rJoint.localAnchorB.set(bodyB.getMount(componentBMountId));
				  rJoint.collideConnected = false; 
				  rJoint.enableLimit = false;
				  gameState.getWorld().createJoint(rJoint);
				 
			} else {
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

	public static TextureRegion assembleObjectImage(GameLoader gameLoader) {
		
		World tempWorld = new World(new Vector2(0, 0f), false);
		tempWorld.setWarmStarting(true);

		GamePhysicalState gameState = new GamePhysicalState(tempWorld, gameLoader);

		CameraManager camera = new CameraManager(Globals.ScreenWidth,
				Globals.ScreenHeight - 50);
		camera.zoom = 0.02f;
		camera.position.set(Globals.ScreenWidth / 2, Globals.ScreenHeight / 2 - 1,
				1);
		camera.update();

		AssembledObject object = assembleObject(gameState);
		object.setPosition(Globals.ScreenWidth / 2, Globals.ScreenHeight / 2);

		tempWorld.step(10, 100, 100);

		FrameBuffer frameBufferObject = new FrameBuffer(Format.RGBA8888,
				Globals.ScreenWidth, Globals.ScreenHeight, false);
	
		SpriteBatch batch = new SpriteBatch();

		batch.setProjectionMatrix(camera.combined);

		frameBufferObject.begin();
		Gdx.gl20.glClearColor(0f, 0f, 0f, 0f); // transparent black
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT); // clear the color buffer

		batch.begin();
		//object.draw(batch);
		object.drawImage(batch);

		batch.end();

		frameBufferObject.end();

		TextureRegion tr = new TextureRegion(
				frameBufferObject.getColorBufferTexture());
		tr.flip(false, true);
		
		tempWorld.dispose();
		//frameBufferObject.dispose();
		batch.dispose();

		return tr;

	}

	static private HashMap<String, Component> extractComponents(
			JSONCar source, GamePhysicalState gameState) {
		//HashMap<String, Component> ret = new HashMap<String, Component>();
		HashMap<String, Component> ret = new HashMap<String, Component>();
		ArrayList<JSONComponent> jcomponents = source.getComponentList();

		Iterator<JSONComponent> iter = jcomponents.iterator();
		JSONComponent sourceComponent;
		Component component = null;
		ArrayList<Component> componentList = null;
		//String componentName;
		JSONComponentName componentName;
		
		while (iter.hasNext()) {
			componentList = null;
			component = null;
			sourceComponent = iter.next();
			//componentName = Globals.getComponentName(sourceComponent
			//		.getComponentName());
			componentName = sourceComponent.getjComponentName();
			Integer partLevel = componentName.getLevel() == null ? 1 : componentName.getLevel();

			

			/*if (componentName.contains(ComponentNames.SPRINGJOINT)) {
				componentList = ComponentBuilder.buildJointComponent(
						componentName, 1, gameState);*/
			
			if (componentName.getBaseName().compareTo(ComponentNames.SPRINGJOINT)==0) {
				componentList = ComponentBuilder.buildJointComponent(
						componentName.getBaseName(), partLevel, gameState);
				
				

				/*String[] nameList = sourceComponent.getComponentName().split(
						NAME_ID_SPLIT);
				String jointComponentName = nameList[0];
				String jointComponentId = nameList[1];*/
				String jointComponentName = sourceComponent.getBaseName();
				String jointComponentId = sourceComponent.getComponentId();
				
				
				Iterator<Component> it = componentList.iterator();
				while (it.hasNext()) {
					Component localComponent = it.next();
					localComponent.setGroup(CAR);
					// localComponent.applyProperties(sourceComponent.getProperties());

				}

				componentList.get(0).applyProperties(
						sourceComponent.getProperties(), PropertyTypes.BOTH);
				componentList.get(1)
						.applyProperties(sourceComponent.getProperties(),
								PropertyTypes.ABSOLUTE);

				/*componentList.get(0).setComponentName(
						jointComponentName + NAME_SUBNAME_SPLIT
								+ ComponentSubNames.UPPER + NAME_ID_SPLIT
								+ jointComponentId);*/
				
				componentList.get(0).setBaseName(jointComponentName);
				componentList.get(0).setSubName(ComponentSubNames.UPPER);
				componentList.get(0).setComponentId(jointComponentId);
				componentList.get(0).setMountId(Integer.toString(0));
				componentList.get(0).setPartLevel(partLevel);

				/*componentList.get(1).setComponentName(
						jointComponentName + NAME_SUBNAME_SPLIT
								+ ComponentSubNames.LOWER + NAME_ID_SPLIT
								+ jointComponentId);*/
				
				componentList.get(1).setBaseName(jointComponentName);
				componentList.get(1).setSubName(ComponentSubNames.LOWER);
				componentList.get(1).setComponentId(jointComponentId);
				componentList.get(1).setMountId(Integer.toString(0));
				componentList.get(1).setPartLevel(partLevel);
				
				 //System.out.println("Assembler:  Extracting springA: " + componentList.get(0).getjComponentName());
				 //System.out.println("Assembler:  Extracting springB: " + componentList.get(1).getjComponentName());

				/*ret.put(componentList.get(0).getComponentName(),
						componentList.get(0));
				ret.put(jointComponentName + NAME_SUBNAME_SPLIT
						+ ComponentSubNames.LOWER + NAME_ID_SPLIT
						+ jointComponentId, componentList.get(1));*/
				
				ret.put(componentList.get(0).getjComponentName().getId(),
						componentList.get(0));
				ret.put(componentList.get(1).getjComponentName().getId(),
						componentList.get(1));
				

			} else {
				
				componentName = sourceComponent.getjComponentName();
				
				//--------------------HACK-----------------------
				if(componentName.getBaseName().compareTo(ComponentNames.AXLE)==0){
					componentName.setBaseName(ComponentNames.TIRE);
				}
				//-----------------------------------------------
				
				//System.out.println("Assembler : sourceComponet: " + componentName);
				
				component = ComponentBuilder.buildComponent(componentName.getBaseName(), partLevel,
						gameState);

				if (sourceComponent.getProperties() != null) {
					component.applyProperties(sourceComponent.getProperties(),
							PropertyTypes.BOTH);
				}
				component.setGroup(CAR);
				//component.setComponentName(sourceComponent.getComponentName());
				//String[] nameList = component.getComponentName().split(
				//		NAME_ID_SPLIT);
				//String jointComponentName = nameList[0];
				//String jointComponentId = nameList[1];
				
				component.setBaseName(sourceComponent.getBaseName());//(jointComponentName);
				component.setComponentId(sourceComponent.getComponentId());//(jointComponentId);
				
				//ret.put(sourceComponent.getComponentName(), component);
				ret.put(sourceComponent.getjComponentName().getId(), component);
			}

		}

		return ret;
	}

	public ArrayList<GroundUnitDescriptor> assembleTrack(String mapString,
			Vector2 offset) {
		JSONTrack jsonTrack = JSONTrack.objectify(mapString);
		ArrayList<Vector2> mapPoints = jsonTrack.getPoints();

		// System.out.println(mapPoints.size());

		ArrayList<GroundUnitDescriptor> retList = new ArrayList<GroundUnitDescriptor>();

		Iterator<Vector2> iter = mapPoints.iterator();
		Vector2 lastPoint, point = null;
		boolean first = true;

		while (iter.hasNext()) {
			if (first) {
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
			GroundUnitDescriptor gud = new GroundUnitDescriptor(lastPoint,
					point, false);// , "texture.png");
			retList.add(gud);

		}

		return retList;
	}

}
