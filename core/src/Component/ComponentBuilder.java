package Component;

import java.util.ArrayList;

import wrapper.BaseActor;
import wrapper.GamePhysicalState;
import Component.Component.ComponentTypes;
import JSONifier.JSONComponentName;
import Menu.MenuBuilder;
import UserPackage.User;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

public class ComponentBuilder {

	public static Component buildComponent(String name, int level,
			GamePhysicalState gameState, boolean forBuilder) {

		if (name.compareTo(ComponentNames.BAR3) == 0) {
			return buildBar3(gameState, level, forBuilder);
		} else if (name.compareTo(ComponentNames.SOLIDJOINT) == 0) {
			return buildSolidJoint(gameState, level, forBuilder);
		} else if (name.compareTo(ComponentNames.TIRE) == 0
				|| name.compareTo(ComponentNames.AXLE) == 0) {
			return buildTire(gameState, level, forBuilder);
		} else if (name.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			return buildSpringJoint(gameState, level, forBuilder, false).get(0);
		} else if (name.compareTo(ComponentNames.LIFE) == 0) {
			return buildLife(gameState, level, forBuilder);
		} else if (name.compareTo(ComponentNames.POST) == 0) {
			return buildTrackPost(gameState, level, forBuilder);
		} else if (name.compareTo(ComponentNames.TRACKBAR) == 0) {
			return buildTrackBar(gameState, level, forBuilder);
		} else if (name.compareTo(ComponentNames.TRACKBALL) == 0) {
			return buildTrackBall(gameState, level, forBuilder);
		} else if (name.compareTo(ComponentNames.TRACKCOIN) == 0) {
			return buildTrackCoin(gameState, level, forBuilder);
		}

		return null;
	}

	public static ArrayList<Component> buildJointComponent(String name,
			int level, GamePhysicalState gameState, boolean forBuilder) {

		if (name.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			return buildSpringJoint(gameState, level, forBuilder, false);
		}

		return null;
	}

	// Builders
	public static Component buildBar3(GamePhysicalState gameState, int level,
			boolean forBuilder) {

		ComponentPhysicsProperties properties = new ComponentPhysicsProperties();
		JSONComponentName componentName = new JSONComponentName();
		componentName.setBaseName(ComponentNames.BAR3);

		int textureLevel = level;

		properties.setDensity(10 * 15 / level);
		properties.setTexture("bar/level" + textureLevel + ".png");

		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(componentName, properties, gameState);

		float mountHeight = tmpActor.getCenter().y;

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(new Vector2(
				tmpActor.getCenter().x - tmpActor.getWidth() / 2, mountHeight));
		mounts.add(new Vector2(tmpActor.getCenter().x, mountHeight));
		mounts.add(new Vector2(
				tmpActor.getCenter().x + tmpActor.getWidth() / 2, mountHeight));
		tmpActor.setMounts(mounts, tmpActor.getWidth() / 2);

		Component tmpComponent = new Component(tmpActor, ComponentTypes.PART,
				componentName);
		tmpComponent.addTexture(MenuBuilder.BUILDER, "bar/builder.png");
		tmpComponent.addTexture(MenuBuilder.BUILDER_SELECTED,
				"bar/builder_selected.png");

		if (forBuilder) {

		}

		return tmpComponent;
	}

	public static Component buildSolidJoint(GamePhysicalState gameState,
			int level, boolean forBuilder) {
		JSONComponentName componentName = new JSONComponentName();
		componentName.setBaseName(ComponentNames.SOLIDJOINT);
		BaseActor tmpActor = new BaseActor(componentName, "solid_joint.png",
				gameState);
		// tmpActor.disablePhysics();
		Component tmpComponent = new Component(tmpActor, ComponentTypes.JOINT,
				componentName);
		return tmpComponent;
	}

	public static Component buildAxle(GamePhysicalState gameState, int level,
			boolean forBuilder) {
		// Build axle
		JSONComponentName componentName = new JSONComponentName();
		componentName.setBaseName(ComponentNames.SOLIDJOINT);
		BaseActor tmpActor = new BaseActor(componentName, gameState);
		Component tmpComponent = new Component(tmpActor, ComponentTypes.JOINT,
				componentName);
		return tmpComponent;
	}

	public static Component buildTire(GamePhysicalState gameState, int level,
			boolean forBuilder) {
		// Setup mounts, shape
		JSONComponentName componentName = new JSONComponentName();
		ComponentPhysicsProperties properties = new ComponentPhysicsProperties();

		properties.setFriction(0.04f * level);
		properties.setDensity(150 - 10*level);
		properties.setTexture("tire/level" + level + ".png");
		properties.setRestituition(0.15f + level*0.01f);
		componentName.setBaseName(ComponentNames.WHEEL);
		BaseActor tmpActor = new BaseActor(componentName, properties, gameState);
		CircleShape shape = new CircleShape();

		shape.setRadius(tmpActor.getWidth() / 2);
		tmpActor.setShapeBase(shape);
		// tmpActor.setScale(1.2f);
		// tmpActor.setPosition(0, 0);
		// tmpActor.setDensity(40);
		componentName.setBaseName(ComponentNames.AXLE);
		ComponentPhysicsProperties axleProperties = new ComponentPhysicsProperties();
		BaseActor fixture = new BaseActor(componentName, axleProperties,
				gameState);
		// fixture.setPosition(tmpActor.getCenter().x, tmpActor.getCenter().y);
		// fixture.setMounts(mounts, 0.0f);
		fixture.setScale(0.3f);
		fixture.setDensity(0);
		// fixture.setSensor();

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(fixture.getCenter());
		fixture.setMounts(mounts, 0.0f);

		RevoluteJointDef revJoint = new RevoluteJointDef();
		revJoint.initialize(fixture.getPhysicsBody(),
				tmpActor.getPhysicsBody(), fixture.getCenter());
		revJoint.collideConnected = false;
		revJoint.enableLimit = false;
		gameState.getWorld().createJoint(revJoint);

		ArrayList<BaseActor> bodies = new ArrayList<BaseActor>();
		bodies.add(tmpActor);
		bodies.add(fixture);

		// Component tireComponent = new Component(tmpActor,
		// ComponentTypes.PART,
		// ComponentNames.WHEEL);

		componentName.setBaseName(ComponentNames.WHEEL);
		Component tireComponent = new Component(tmpActor, ComponentTypes.PART,
				componentName);
		tireComponent.addTexture(MenuBuilder.BUILDER, "tire/builder.png");
		tireComponent.addTexture(MenuBuilder.BUILDER_SELECTED,
				"tire/builder_selected.png");
		tireComponent.setJointBodies(bodies);

		componentName.setBaseName(ComponentNames.AXLE);
		Component axleComponent = new Component(fixture, ComponentTypes.PART,
				componentName);
		axleComponent.setJointBodies(bodies);

		return axleComponent;
	}

	public static ArrayList<Component> buildSpringJoint(
			GamePhysicalState gameState, int level, boolean forBuilder,
			boolean HACK_sizingForPreASMcar) {

		JSONComponentName componentNameUpper = new JSONComponentName();
		componentNameUpper.setBaseName(ComponentNames.SPRINGJOINT);
		componentNameUpper.setSubName(ComponentSubNames.UPPER);

		ComponentPhysicsProperties properties = new ComponentPhysicsProperties();
		properties.setDensity(10);
		if (forBuilder) {
			properties.setTexture("spring_upper/level" + level + ".png");
		} else {
			properties.setTexture("spring_lower/level" + level + ".png");
		}

		float springHeight = 1.8f;
		float springTravelUpper = 0.1f;
		float springTravelLower = 0.8f;
		float adjust = 2.3f;

		float height = 0;
		if (forBuilder) {
			height = (float) (1.5 / 2);
			springHeight = 1.1f;
		}

		BaseActor topFixture = new BaseActor(componentNameUpper, properties,
				gameState);

		topFixture.addSprite(MenuBuilder.BUILDER, "spring_upper/builder.png");
		topFixture.addSprite(MenuBuilder.BUILDER_SELECTED,
				"spring_upper/builder_selected.png");

		ArrayList<Vector2> mountTop = new ArrayList<Vector2>();
		mountTop.add(topFixture.getCenter());
		// mountTop.add(new
		// Vector2(topFixture.getCenter().x+0.1f,topFixture.getCenter().y+0.1f));
		topFixture.setMounts(mountTop, 0.0f);
		topFixture.setScaleY(1.2f);
		topFixture.setScaleY(MenuBuilder.BUILDER, 1.2f);
		topFixture.setScaleY(MenuBuilder.BUILDER_SELECTED, 1.2f);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.3f, 0.5f, new Vector2(0, -0.2f), 0);
		topFixture.setShapeBase(shape);
		// topFixture.setSensor();
		topFixture.setDensity(1);
		// topFixture.setScaleY(0.5f);
		//

		if (forBuilder && !HACK_sizingForPreASMcar) {
			topFixture.setPosition(0, height);

			/*
			 * FixtureDef fixtureDef = new FixtureDef(); PolygonShape shape =
			 * new PolygonShape(); shape.setAsBox(0.3f, 0.5f,new
			 * Vector2(0,-0.2f),0); fixtureDef.shape = shape; fixtureDef.density
			 * = 0; fixtureDef.isSensor = true;
			 * 
			 * topFixture.getPhysicsBody().createFixture(fixtureDef);
			 */
		} else {
			adjust = 1;
		}

		JSONComponentName componentNameLower = new JSONComponentName();
		componentNameLower.setBaseName(ComponentNames.SPRINGJOINT);
		componentNameLower.setSubName(ComponentSubNames.LOWER);
		if (forBuilder) {
			properties.setTexture("spring_lower/level" + level + ".png");
		} else {
			properties.setTexture("spring_upper/level" + level + ".png");
		}

		BaseActor botFixture = new BaseActor(componentNameLower, properties,
				gameState);
		botFixture.addSprite(MenuBuilder.BUILDER, "spring_lower/builder.png");
		botFixture.addSprite(MenuBuilder.BUILDER_SELECTED,
				"spring_lower/builder_selected.png");

		/*
		 * BaseActor botFixture = new BaseActor( ComponentNames.SPRINGJOINT +
		 * Assembler.NAME_SUBNAME_SPLIT + ComponentSubNames.LOWER,
		 * "suspension_upper.png", gameState);
		 */
		//
		ArrayList<Vector2> mountBot = new ArrayList<Vector2>();
		mountBot.add(botFixture.getCenter());
		botFixture.setMounts(mountBot, 0.0f);
		botFixture.setScaleY(1.5f);
		botFixture.setScaleY(MenuBuilder.BUILDER, 1.25f);
		botFixture.setScaleY(MenuBuilder.BUILDER_SELECTED, 1.25f);
		shape.setAsBox(0.3f, 0.5f, new Vector2(0, 0.2f), 0);
		botFixture.setShapeBase(shape);
		// botFixture.setScale(0.3f);
		// botFixture.setSensor();
		botFixture.setDensity(1);
		// botFixture.setScaleY(0.5f);

		if (forBuilder && !HACK_sizingForPreASMcar) {
			botFixture.setPosition(0, -height);

			/*
			 * FixtureDef fixtureDef = new FixtureDef(); PolygonShape shape =
			 * new PolygonShape(); shape.setAsBox(0.3f, 0.5f,new
			 * Vector2(0,0.2f),0); fixtureDef.shape = shape; fixtureDef.density
			 * = 0; fixtureDef.isSensor = true;
			 * 
			 * botFixture.getPhysicsBody().createFixture(fixtureDef);
			 */
		}

		// botFixture.setOrigin(new
		// Vector2(botFixture.getWidth()/2,botFixture.getHeight()/2));

		ArrayList<BaseActor> bodies = new ArrayList<BaseActor>();
		bodies.add(topFixture);
		bodies.add(botFixture);

		DistanceJointDef dJoint = new DistanceJointDef();
		dJoint.initialize(topFixture.getPhysicsBody(),
				botFixture.getPhysicsBody(), topFixture.getCenter(),
				botFixture.getCenter());
		dJoint.length = springHeight;
		dJoint.collideConnected = false;
		if (!forBuilder) {
			/*-------------------PHYSICS-------------------------*/
			// smaller softer
			
			// Controls stiffness, larger means harder
			dJoint.frequencyHz = 7.5f + (User.MAX_SPRING_LEVEL - level)*1.2f;// 10
			
			// controls recoil, smaller comes back faster
			dJoint.dampingRatio = 0.1f + (User.MAX_SPRING_LEVEL - level)*0.12f;// 0.5f
		}
		//
		gameState.getWorld().createJoint(dJoint);

		//

		if (forBuilder) {

			WeldJointDef wJoint = new WeldJointDef();

			wJoint.initialize(topFixture.getPhysicsBody(),
					botFixture.getPhysicsBody(), topFixture.getMount(0));
			wJoint.localAnchorA.set(new Vector2(topFixture.getMount(0).x,
					topFixture.getMount(0).y - springHeight * adjust));
			wJoint.localAnchorB.set(botFixture.getMount(0));
			wJoint.collideConnected = false;
			gameState.getWorld().createJoint(wJoint);

		} else {
			PrismaticJointDef rJoint = new PrismaticJointDef();
			rJoint.initialize(topFixture.getPhysicsBody(),
					botFixture.getPhysicsBody(), botFixture.getCenter(),
					new Vector2(0, 1));
			rJoint.localAnchorA.set(topFixture.getCenter());
			rJoint.localAnchorB.set(botFixture.getCenter());
			rJoint.referenceAngle = 0;
			rJoint.lowerTranslation = springHeight - springTravelLower;
			rJoint.upperTranslation = springHeight + springTravelUpper;
			rJoint.enableLimit = true;
			rJoint.enableMotor = true;
			rJoint.collideConnected = false;
			gameState.getWorld().createJoint(rJoint);

		}

		// if(!forBuilder)

		Component topComponent = new Component(topFixture,
				ComponentTypes.JOINT, componentNameUpper);
		topComponent.setJointBodies(bodies);

		Component botComponent = new Component(botFixture,
				ComponentTypes.JOINT, componentNameLower);
		botComponent.setJointBodies(bodies);

		ArrayList<Component> retList = new ArrayList<Component>();
		retList.add(botComponent);
		retList.add(topComponent);

		return retList;
	}

	public static Component buildLife(GamePhysicalState gameState, int level,
			boolean forBuilder) {

		JSONComponentName componentName = new JSONComponentName();
		componentName.setBaseName(ComponentNames.LIFE);

		ComponentPhysicsProperties properties = new ComponentPhysicsProperties();

		properties.setDensity(5);
		properties.setTexture("life_small.png");
		properties.setSetFixtureData(true);

		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(componentName, properties, gameState);
		tmpActor.addSprite(MenuBuilder.BUILDER, "life/builder.png");
		tmpActor.addSprite(MenuBuilder.BUILDER_SELECTED,
				"life/builder_selected.png");

		tmpActor.setDensity(5);
		tmpActor.setScale(0.6f);
		tmpActor.setScale(MenuBuilder.BUILDER, 0.6f);
		tmpActor.setScale(MenuBuilder.BUILDER_SELECTED, 0.6f);

		if (!forBuilder) {
			CircleShape shape = new CircleShape();
			shape.setRadius(0.5f);

			tmpActor.setShapeBase(shape);
		}

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(new Vector2(tmpActor.getCenter().x, tmpActor.getCenter().y
				- tmpActor.getHeight()));

		tmpActor.setMounts(mounts, tmpActor.getWidth() / 2);

		Component tmpComponent = new Component(tmpActor, ComponentTypes.PART,
				componentName);

		// componentName.setBaseName(ComponentNames.LIFE + NAME_SUBNAME_SPLIT
		// + ComponentNames.CAMERAFOCUS);
		componentName.setBaseName(ComponentNames.LIFE);
		componentName.setSubName(ComponentNames.CAMERAFOCUS);

		BaseActor cameraActor = new BaseActor(componentName, gameState);
		cameraActor.setMounts(mounts, tmpActor.getWidth() / 2);
		// cameraActor.setDensity(1f);
		cameraActor.setScale(0.2f);
		cameraActor.setSensor();

		Component camerafocus = new Component(cameraActor, ComponentTypes.PART,
				componentName);

		RopeJointDef dJoint = new RopeJointDef();
		dJoint.bodyA = tmpActor.getPhysicsBody();
		dJoint.bodyB = cameraActor.getPhysicsBody();
		dJoint.localAnchorA.set(tmpActor.getCenter());
		dJoint.localAnchorB.set(cameraActor.getCenter());
		dJoint.maxLength = 1f;

		/*
		 * DistanceJointDef dJoint = new DistanceJointDef();
		 * dJoint.initialize(tmpActor.getPhysicsBody(),
		 * cameraActor.getPhysicsBody(), tmpActor.getCenter(),
		 * cameraActor.getCenter()); dJoint.length = 1f; dJoint.collideConnected
		 * = false; if (!forBuilder) { dJoint.frequencyHz = 10000;
		 * dJoint.dampingRatio = 1000f; }
		 */
		gameState.getWorld().createJoint(dJoint);

		PrismaticJointDef rJoint = new PrismaticJointDef();
		rJoint.initialize(tmpActor.getPhysicsBody(),
				cameraActor.getPhysicsBody(), cameraActor.getCenter(),
				new Vector2(0, 1));
		rJoint.localAnchorA.set(tmpActor.getCenter());
		rJoint.localAnchorB.set(cameraActor.getCenter());
		rJoint.enableMotor = true;
		rJoint.collideConnected = false;
		rJoint.lowerTranslation = -1f;
		rJoint.upperTranslation = 1f;
		if (!forBuilder) {
			// rJoint.lowerTranslation = springHeight - springTravel;
			// rJoint.upperTranslation = springHeight + springTravel;
			rJoint.enableLimit = true;
		} else {
			// rJoint.lowerTranslation = springHeight;
			// rJoint.upperTranslation = springHeight;
			rJoint.enableLimit = false;
		}

		gameState.getWorld().createJoint(rJoint);

		ArrayList<BaseActor> bodies = new ArrayList<BaseActor>();
		bodies.add(tmpActor);
		bodies.add(cameraActor);

		camerafocus.setJointBodies(bodies);

		tmpComponent.setJointBodies(bodies);

		if (forBuilder) {
			// nothing
		}

		return tmpComponent;
	}

	public static Component buildTrackPost(GamePhysicalState gamePhysicalState,
			int partLevel, boolean forBuilder) {

		ComponentPhysicsProperties properties = new ComponentPhysicsProperties();
		JSONComponentName componentName = new JSONComponentName();
		componentName.setBaseName(ComponentNames.POST);

		properties.setDensity(partLevel);
		properties.setTexture("temp_post.png");

		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(componentName, properties,
				gamePhysicalState);

		tmpActor.setBodyType(BodyType.StaticBody);

		float mountHeight = tmpActor.getCenter().y;

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(new Vector2(tmpActor.getCenter().x, mountHeight));
		tmpActor.setMounts(mounts, tmpActor.getWidth() / 2);

		Component tmpComponent = new Component(tmpActor, ComponentTypes.PART,
				componentName);

		if (forBuilder) {

		}

		return tmpComponent;
	}

	public static Component buildTrackBar(GamePhysicalState gamePhysicalState,
			int partLevel, boolean forBuilder) {

		ComponentPhysicsProperties properties = new ComponentPhysicsProperties();
		JSONComponentName componentName = new JSONComponentName();
		componentName.setBaseName(ComponentNames.TRACKBAR);

		properties.setDensity(5);
		properties.setFriction(1);
		properties.setTexture("temp_bar.png");

		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(componentName, properties,
				gamePhysicalState);

		float mountHeight = tmpActor.getCenter().y;

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(new Vector2(tmpActor.getCenter().x - tmpActor.getWidth() / 2
				+ 0.1f, mountHeight));
		mounts.add(new Vector2(tmpActor.getCenter().x, mountHeight));
		mounts.add(new Vector2(tmpActor.getCenter().x + tmpActor.getWidth() / 2
				- 0.1f, mountHeight));
		tmpActor.setMounts(mounts, tmpActor.getWidth() / 2);

		Component tmpComponent = new Component(tmpActor, ComponentTypes.PART,
				componentName);

		if (forBuilder) {

		}

		return tmpComponent;
	}

	public static Component buildTrackBall(GamePhysicalState gamePhysicalState,
			int partLevel, boolean forBuilder) {

		ComponentPhysicsProperties properties = new ComponentPhysicsProperties();
		JSONComponentName componentName = new JSONComponentName();
		componentName.setBaseName(ComponentNames.TRACKBALL);

		properties.setDensity(8);
		properties.setRestituition(0.6f);
		properties.setFriction(0.7f);
		properties.setTexture("solid_joint.png");

		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(componentName, properties,
				gamePhysicalState);

		CircleShape shape = new CircleShape();
		shape.setRadius(0.2f);

		tmpActor.setShapeBase(shape);
		Component tmpComponent = new Component(tmpActor, ComponentTypes.PART,
				componentName);

		if (forBuilder) {

		}

		return tmpComponent;
	}

	public static Component buildTrackCoin(GamePhysicalState gamePhysicalState,
			int partLevel, boolean forBuilder) {

		ComponentPhysicsProperties properties = new ComponentPhysicsProperties();
		JSONComponentName componentName = new JSONComponentName();
		componentName.setBaseName(ComponentNames.TRACKCOIN);

		properties.setDensity(8);
		properties.setRestituition(0.6f);
		properties.setFriction(0.7f);
		properties.setTexture("coin.png");
		properties.setSetFixtureData(true);

		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(componentName, properties,
				gamePhysicalState);

		tmpActor.setScale(2);

		CircleShape shape = new CircleShape();
		shape.setRadius(0.3f);

		tmpActor.setShapeBase(shape);

		tmpActor.setSensor();
		tmpActor.setBodyType(BodyType.StaticBody);
		Component tmpComponent = new Component(tmpActor, ComponentTypes.PART,
				componentName);

		if (forBuilder) {

		}

		return tmpComponent;
	}

	public static FixtureDef buildMount(Vector2 mount, int level,
			boolean forBuilder) {
		FixtureDef fix = new FixtureDef();

		CircleShape shape = new CircleShape();
		shape.setRadius(0.1f * level);
		shape.setPosition(mount);
		fix.isSensor = true;
		fix.shape = shape;

		return fix;
	}

}
