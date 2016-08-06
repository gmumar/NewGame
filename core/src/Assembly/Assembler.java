package Assembly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import wrapper.BaseActor;
import wrapper.CameraManager;
import wrapper.GamePhysicalState;
import wrapper.Globals;
import Assembly.ColliderCategories.ColliderGroups;
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
import JSONifier.JSONParentClass;
import JSONifier.JSONTrack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Array;
import com.gudesigns.climber.GameLoader;

public class Assembler {

	// final public static String NAME_MOUNT_SPLIT = ":";
	// final public static String NAME_ID_SPLIT = "-";
	// final public static String NAME_SUBNAME_SPLIT = "=";

	// final private static short CAR = -2;

	public static AssembledObject assembleCar(GamePhysicalState gameState,
			String inputString, ColliderGroups group, boolean forBuilder) {
		Integer jointType = 0;

		AssembledObject obj = new AssembledObject();
		/*
		 * Preferences prefs = Gdx.app
		 * .getPreferences(GamePreferences.CAR_PREF_STR);
		 * 
		 * String inputString = prefs .getString( GamePreferences.CAR_MAP_STR,
		 * Globals.defualt_car);
		 */

		JSONCar source = new JSONCar();
		source = JSONCar.objectify(inputString);

		// HashMap<String, Component> parts = extractComponents(source,
		// gameState);
		HashMap<String, Component> parts = extractComponents(source, gameState,
				group, forBuilder);
		// Read the JSONJoint array and build the obj
		ArrayList<JSONJoint> jcomponents = source.getJointList();
		
		Collections.sort(jcomponents, new Comparator<JSONJoint>() {

			@Override
			public int compare(JSONJoint o1, JSONJoint o2) {
				
				String part1a, part2a;
				part1a = o1.m1.getBaseName();
				part2a = o2.m1.getBaseName();

				int result = JSONComponent.getBaseNameIndex(part1a).compareTo(JSONComponent.getBaseNameIndex(part2a));

				if (result == 0) {
					return o1.m1.getComponentId().compareTo(o2.m1.getComponentId());
				} else {

					return result;
				}
			}

		});

		Map<String, Integer> jointTypeList = source.getJointTypeList();

		Integer jointIndex = 0, jointIndexOpponent = 0, currentIndex;
		for (JSONJoint join : jcomponents) {
			

			if (join == null || join.getMount1() == null
					|| join.getMount2() == null)
				continue;

			/*
			 * String componentAName = Globals.parseName(join.m1)[0]; //
			 * BaseActor bodyA = parts.get(componentAName).getObject(); int
			 * componentAMountId = Globals.getMountId(join.m1);
			 */

			String componentAName = join.getMount1().getId();
			// join.getMount1().getMountedId());

			// if(parts.get(componentAName) == null) continue;

			BaseActor bodyA = parts.get(componentAName).getObject();

			if (join.getMount1().getMountId().contains("*"))
				continue;

			int componentAMountId = Integer.parseInt(join.getMount1()
					.getMountId());

			/*
			 * String componentBName = Globals.parseName(join.m2)[0]; //
			 * BaseActor bodyB = parts.get(componentBName).getObject(); int
			 * componentBMountId = Globals.getMountId(join.m2);
			 */
			if (join.getMount2().getMountId().contains("*"))
				continue;

			String componentBName = join.getMount2().getId();
			// if(parts.get(componentBName) == null) continue;

			BaseActor bodyB = parts.get(componentBName).getObject();
			int componentBMountId = Integer.parseInt(join.getMount2()
					.getMountId());

			jointType = jointTypeList.get(join.getMount1().getMountedId());
			
			if(group == ColliderGroups.OPPONENT_CAR){
				jointIndexOpponent--;
				currentIndex = jointIndexOpponent;
			} else {
				jointIndex++;
				currentIndex = jointIndex;
			}

			if (jointType != null && jointType == Globals.ROTATABLE_JOINT) {

				RevoluteJointDef rJoint = new RevoluteJointDef();

				rJoint.initialize(bodyA.getPhysicsBody(),
						bodyB.getPhysicsBody(),
						bodyB.getMount(componentBMountId));
				rJoint.localAnchorA.set(bodyA.getMount(componentAMountId));
				rJoint.localAnchorB.set(bodyB.getMount(componentBMountId));
				rJoint.collideConnected = false;
				rJoint.enableLimit = false;
				gameState.getWorld().createJoint(rJoint).setUserData(currentIndex);

			} else {
				WeldJointDef wJoint = new WeldJointDef();

				wJoint.initialize(bodyA.getPhysicsBody(),
						bodyB.getPhysicsBody(),
						bodyB.getMount(componentBMountId));
				wJoint.localAnchorA.set(bodyA.getMount(componentAMountId));
				wJoint.localAnchorB.set(bodyB.getMount(componentBMountId));
				wJoint.collideConnected = false;
				gameState.getWorld().createJoint(wJoint).setUserData(currentIndex);
			}
			

		}

		obj.setPartList(new ArrayList<Component>(parts.values()));
		obj.setLifeBasePart();
		return obj;
	}

	public static TextureRegion assembleCarImage(GameLoader gameLoader,
			String inputString, boolean forBuilder, boolean forOpponent) {

		World tempWorld = new World(new Vector2(0, 0), false);
		tempWorld.setWarmStarting(true);

		GamePhysicalState gameState = new GamePhysicalState(tempWorld,
				gameLoader);

		CameraManager camera = new CameraManager(
				Globals.CAR_DISPLAY_BUTTON_CAMERA_HEIGHT,
				Globals.CAR_DISPLAY_BUTTON_WIDTH);
		camera.zoom = 0.06f;
		camera.position.set(Globals.ScreenWidth / 2,
				Globals.ScreenHeight / 2 - 1, 1);
		camera.update();

		AssembledObject object = assembleCar(gameState, inputString,
				ColliderGroups.USER_CAR, forBuilder);

		object.setPosition(Globals.ScreenWidth / 2, Globals.ScreenHeight / 2);

		tempWorld.step(10, 100, 100);

		FrameBuffer frameBufferObject = new FrameBuffer(Format.RGBA8888,
				Globals.ScreenWidth, Globals.ScreenHeight, false);

		SpriteBatch batch = new SpriteBatch();

		batch.setProjectionMatrix(camera.combined);

		frameBufferObject.begin();

		if (forOpponent) {
			Gdx.gl20.glClearColor(Globals.GREY.r, Globals.GREY.g,
					Globals.GREY.b, 0); // transparent
			// black
		} else {
			Gdx.gl20.glClearColor(Globals.GREY.r, Globals.GREY.g,
					Globals.GREY.b, 1); // transparent
			// black
		}
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT); // clear the color buffer

		batch.begin();
		// object.draw(batch);
		object.drawImage(batch);

		batch.end();

		frameBufferObject.end();

		TextureRegion tr = new TextureRegion(
				frameBufferObject.getColorBufferTexture());
		tr.flip(false, true);

		tempWorld.dispose();
		// frameBufferObject.dispose();
		batch.dispose();

		return tr;

	}

	static public HashMap<String, Component> extractComponents(
			JSONParentClass source, GamePhysicalState gameState,
			ColliderGroups group, boolean forBuilder) {
		// HashMap<String, Component> ret = new HashMap<String, Component>();
		HashMap<String, Component> ret = new HashMap<String, Component>();
		ArrayList<JSONComponent> jcomponents = source.getComponentList();

		Collections.sort(jcomponents, new Comparator<JSONComponent>() {

			@Override
			public int compare(JSONComponent o1, JSONComponent o2) {

				int result = JSONComponent.getBaseNameIndex(o1.getBaseName()).compareTo(
						JSONComponent.getBaseNameIndex(o1.getBaseName()));

				if (result == 0) {
					return o1.getComponentId().compareTo(o2.getComponentId());
				} else {

					return result;
				}
			}

		});

		Component component = null;
		ArrayList<Component> componentList = null;
		// String componentName;
		JSONComponentName componentName;

		for (JSONComponent sourceComponent : jcomponents) {
			componentList = null;
			component = null;
			// componentName = Globals.getComponentName(sourceComponent
			// .getComponentName());
			componentName = sourceComponent.getjComponentName();

			if (componentName == null)
				continue;

			Integer partLevel = componentName.getLevel() == null ? 1
					: componentName.getLevel();

			/*
			 * if (componentName.contains(ComponentNames.SPRINGJOINT)) {
			 * componentList = ComponentBuilder.buildJointComponent(
			 * componentName, 1, gameState);
			 */

			if (componentName.getBaseName().compareTo(
					ComponentNames.SPRINGJOINT) == 0) {
				componentList = ComponentBuilder.buildJointComponent(
						componentName.getBaseName(), partLevel, gameState,
						forBuilder);

				/*
				 * String[] nameList = sourceComponent.getComponentName().split(
				 * NAME_ID_SPLIT); String jointComponentName = nameList[0];
				 * String jointComponentId = nameList[1];
				 */
				String jointComponentName = sourceComponent.getBaseName();
				String jointComponentId = sourceComponent.getComponentId();

				Iterator<Component> it = componentList.iterator();
				while (it.hasNext()) {
					Component localComponent = it.next();
					// if (source.getParentType() == JSONParentType.CAR) {
					setUpColliders(localComponent, group);
					// localComponent.applyProperties(sourceComponent.getProperties());

				}

				componentList.get(0).applyProperties(
						sourceComponent.getProperties(), PropertyTypes.BOTH);
				componentList.get(1)
						.applyProperties(sourceComponent.getProperties(),
								PropertyTypes.ABSOLUTE);

				/*
				 * componentList.get(0).setComponentName( jointComponentName +
				 * NAME_SUBNAME_SPLIT + ComponentSubNames.UPPER + NAME_ID_SPLIT
				 * + jointComponentId);
				 */

				componentList.get(0).setBaseName(jointComponentName);
				componentList.get(0).setSubName(ComponentSubNames.UPPER);
				componentList.get(0).setComponentId(jointComponentId);
				componentList.get(0).setMountId(Integer.toString(0));
				componentList.get(0).setPartLevel(partLevel);

				/*
				 * componentList.get(1).setComponentName( jointComponentName +
				 * NAME_SUBNAME_SPLIT + ComponentSubNames.LOWER + NAME_ID_SPLIT
				 * + jointComponentId);
				 */

				componentList.get(1).setBaseName(jointComponentName);
				componentList.get(1).setSubName(ComponentSubNames.LOWER);
				componentList.get(1).setComponentId(jointComponentId);
				componentList.get(1).setMountId(Integer.toString(0));
				componentList.get(1).setPartLevel(partLevel);

				/*
				 * ret.put(componentList.get(0).getComponentName(),
				 * componentList.get(0)); ret.put(jointComponentName +
				 * NAME_SUBNAME_SPLIT + ComponentSubNames.LOWER + NAME_ID_SPLIT
				 * + jointComponentId, componentList.get(1));
				 */

				ret.put(componentList.get(0).getjComponentName().getId(),
						componentList.get(0));
				ret.put(componentList.get(1).getjComponentName().getId(),
						componentList.get(1));

			} else {

				componentName = sourceComponent.getjComponentName();

				// --------------------HACK-----------------------
				if (componentName.getBaseName().compareTo(ComponentNames.AXLE) == 0) {
					componentName.setBaseName(ComponentNames.TIRE);
				}
				// -----------------------------------------------

				component = ComponentBuilder.buildComponent(
						componentName.getBaseName(), partLevel, gameState,
						forBuilder);

				if (sourceComponent.getProperties() != null) {
					component.applyProperties(sourceComponent.getProperties(),
							PropertyTypes.BOTH);
				}

				setUpColliders(component, group);

				// component.setComponentName(sourceComponent.getComponentName());
				// String[] nameList = component.getComponentName().split(
				// NAME_ID_SPLIT);
				// String jointComponentName = nameList[0];
				// String jointComponentId = nameList[1];

				component.setBaseName(sourceComponent.getBaseName());// (jointComponentName);
				component.setComponentId(sourceComponent.getComponentId());// (jointComponentId);

				// ret.put(sourceComponent.getComponentName(), component);
				ret.put(sourceComponent.getjComponentName().getId(), component);
			}

		}

		return ret;
	}

	private static void setUpColliders(Component component, ColliderGroups group) {
		if (group == ColliderGroups.USER_CAR) {
			// component.setGroup(CAR);
			component
					.setFilter(
							ColliderCategories.CAR,
							(short) (ColliderCategories.GROUND_PART | ColliderCategories.GROUND));
		} else if (group == ColliderGroups.OPPONENT_CAR) {
			if (component.getBaseName().compareTo(ComponentNames.LIFE) == 0) {
				component.setFilter(ColliderCategories.OPPONENT_CAR,
						(short) (ColliderCategories.OPPONENT_GROUND_PART));
			} else {
				component
						.setFilter(
								ColliderCategories.OPPONENT_CAR,
								(short) (ColliderCategories.OPPONENT_GROUND_PART | ColliderCategories.GROUND));
			}
		} else if (group == ColliderGroups.OPPONENT_GROUND) {
			if (component.getBaseName()
					.compareTo(ComponentNames.TOUCHABLE_POST) == 0) {
				component.setFilter(
						ColliderCategories.OPPONENT_TOUCHABLE_GROUND_PART,
						ColliderCategories.OPPONENT_GROUND_PART);
			} else {
				component
						.setFilter(
								ColliderCategories.OPPONENT_GROUND_PART,
								(short) (ColliderCategories.OPPONENT_CAR | ColliderCategories.OPPONENT_TOUCHABLE_GROUND_PART));
			}
		} else {
			if (component.getBaseName()
					.compareTo(ComponentNames.TOUCHABLE_POST) == 0) {
				component.setFilter(ColliderCategories.TOUCHABLE_GROUND_PART,
						ColliderCategories.GROUND_PART);
			} else {
				component
						.setFilter(
								ColliderCategories.GROUND_PART,
								(short) (ColliderCategories.CAR | ColliderCategories.TOUCHABLE_GROUND_PART));
			}
		}
	}

	public AssembledTrack assembleTrack(String mapString,
			GamePhysicalState gameState, Vector2 offset,
			boolean buildForMainMenu, boolean forReplay) {
		Integer jointType = 0;

		JSONTrack jsonTrack = JSONTrack.objectify(mapString);
		jsonTrack.applyOffset(offset);

		ArrayList<Vector2> mapPoints = jsonTrack.getPoints();
		Collection<Component> partsCollection = new ArrayList<Component>();
		ArrayList<ColliderGroups> partSets = new ArrayList<ColliderGroups>();
		partSets.add(ColliderGroups.USER_GROUND); 

		if (forReplay) {
			partSets.add(ColliderGroups.OPPONENT_GROUND);
		}

		for (ColliderGroups group : partSets) {
			HashMap<String, Component> parts = extractComponents(jsonTrack,
					gameState, group, buildForMainMenu);

			if (group == ColliderGroups.OPPONENT_GROUND) {
				for (Component part : parts.values()) {
					if(part.getBaseName().compareTo(ComponentNames.TRACKCOIN)!=0){
						part.setAlpha(0.5f);
						partsCollection.add(part);
					}
					
				}
			} else {
				partsCollection.addAll(parts.values());
			}

			if (buildForMainMenu) {
				for (Component part : partsCollection) {
					part.getObject().setSensor();
				}
			}

			ArrayList<JSONJoint> jcomponents = jsonTrack.getJointList();
			
			Collections.sort(jcomponents, new Comparator<JSONJoint>() {

				@Override
				public int compare(JSONJoint o1, JSONJoint o2) {
					
					String part1a, part2a;
					part1a = o1.m1.getBaseName();
					part2a = o2.m1.getBaseName();

					int result = JSONComponent.getBaseNameIndex(part1a).compareTo(JSONComponent.getBaseNameIndex(part2a));

					if (result == 0) {
						return o1.m1.getComponentId().compareTo(o2.m1.getComponentId());
					} else {

						return result;
					}
				}

			});

			HashMap<String, Integer> jointTypeList = jsonTrack
					.getComponentJointTypes();

			for (JSONJoint join : jcomponents) {

				/*
				 * String componentAName = Globals.parseName(join.m1)[0]; //
				 * BaseActor bodyA = parts.get(componentAName).getObject(); int
				 * componentAMountId = Globals.getMountId(join.m1);
				 */

				String componentAName = join.getMount1().getId();
				// if(parts.get(componentAName) == null) continue;

				BaseActor bodyA = parts.get(componentAName).getObject();

				// if(join.getMount1().getMountId().contains("*")) continue;

				int componentAMountId = Integer.parseInt(join.getMount1()
						.getMountId());

				/*
				 * String componentBName = Globals.parseName(join.m2)[0]; //
				 * BaseActor bodyB = parts.get(componentBName).getObject(); int
				 * componentBMountId = Globals.getMountId(join.m2);
				 */
				// if(join.getMount2().getMountId().contains("*")) continue;

				String componentBName = join.getMount2().getId();
				// if(parts.get(componentBName) == null) continue;

				BaseActor bodyB = parts.get(componentBName).getObject();
				int componentBMountId = Integer.parseInt(join.getMount2()
						.getMountId());

				jointType = jointTypeList.get(join.getMount1().getMountedId());

				if (jointType == Globals.ROTATABLE_JOINT) {

					RevoluteJointDef rJoint = new RevoluteJointDef();

					rJoint.initialize(bodyA.getPhysicsBody(),
							bodyB.getPhysicsBody(),
							bodyB.getMount(componentBMountId));
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
		}

		ArrayList<GroundUnitDescriptor> retList = new ArrayList<GroundUnitDescriptor>();

		Iterator<Vector2> iter = mapPoints.iterator();
		Vector2 lastPoint, point = null;
		boolean first = true;

		while (iter.hasNext()) {
			if (first) {
				point = iter.next();
				first = !first;
				continue;
			}
			lastPoint = point;
			point = iter.next();

			GroundUnitDescriptor gud = new GroundUnitDescriptor(lastPoint,
					point, false);// , "texture.png");
			retList.add(gud);

		}

		return new AssembledTrack(retList, partsCollection);
	}

}
