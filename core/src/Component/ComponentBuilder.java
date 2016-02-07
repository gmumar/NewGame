package Component;

import java.util.ArrayList;

import wrapper.BaseActor;
import wrapper.GameState;
import Assembly.Assembler;
import Component.Component.ComponentTypes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

public class ComponentBuilder {

	public static Component buildComponent(String name, GameState gameState) {

		if (name.compareTo(ComponentNames.BAR3) == 0) {
			return buildBar3(gameState, false);
		} else if (name.compareTo(ComponentNames.SOLIDJOINT) == 0) {
			return buildSolidJoint(gameState, false);
		} else if (name.compareTo(ComponentNames.TIRE) == 0) {
			return buildTire(gameState, false);
		} else if (name.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			return buildSpringJoint(gameState, false).get(0);
		} else if (name.compareTo(ComponentNames.LIFE) == 0) {
			return buildLife(gameState, false);
		}

		return null;
	}

	public static ArrayList<Component> buildJointComponent(String name,
			GameState gameState) {

		if (name.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			return buildSpringJoint(gameState, false);
		}

		return null;
	}

	// Builders
	public static Component buildBar3(GameState gameState, boolean forBuilder) {
		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(ComponentNames.BAR3,
				"temp_bar.png", gameState);

		float mountHeight = tmpActor.getCenter().y;

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(new Vector2(
				tmpActor.getCenter().x - tmpActor.getWidth() / 2, mountHeight));
		mounts.add(new Vector2(tmpActor.getCenter().x, mountHeight));
		mounts.add(new Vector2(
				tmpActor.getCenter().x + tmpActor.getWidth() / 2, mountHeight));
		tmpActor.setMounts(mounts, tmpActor.getWidth() / 2);

		Component tmpComponent = new Component(tmpActor, ComponentTypes.PART,
				ComponentNames.BAR3);

		if (forBuilder) {

		}

		return tmpComponent;
	}

	public static Component buildSolidJoint(GameState gameState,
			boolean forBuilder) {
		BaseActor tmpActor = new BaseActor(ComponentNames.SOLIDJOINT,
				"solid_joint.png", gameState);
		// tmpActor.disablePhysics();
		Component tmpComponent = new Component(tmpActor, ComponentTypes.JOINT,
				ComponentNames.SOLIDJOINT);
		return tmpComponent;
	}

	public static Component buildAxle(GameState gameState, boolean forBuilder) {
		// Build axle
		BaseActor tmpActor = new BaseActor(ComponentNames.AXLE,
				gameState);
		Component tmpComponent = new Component(tmpActor, ComponentTypes.JOINT,
				ComponentNames.SOLIDJOINT);
		return tmpComponent;
	}

	public static Component buildTire(GameState gameState, boolean forBuilder) {
		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(ComponentNames.WHEEL,
				"temp_tire_2.png", gameState);
		CircleShape shape = new CircleShape();

		tmpActor.setRestitution(20f);
		// tmpActor.setScale(1.2f);
		// tmpActor.setPosition(0, 0);
		tmpActor.setDensity(40);
		shape.setRadius(tmpActor.getWidth() / 2);
		tmpActor.setShapeBase(shape);

		BaseActor fixture = new BaseActor(ComponentNames.AXLE,
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

		Component tireComponent = new Component(tmpActor, ComponentTypes.PART,
				ComponentNames.WHEEL);
		tireComponent.setJointBodies(bodies);

		Component axleComponent = new Component(fixture, ComponentTypes.PART,
				ComponentNames.AXLE);
		axleComponent.setJointBodies(bodies);

		return axleComponent;
	}

	public static ArrayList<Component> buildSpringJoint(GameState gameState,
			boolean forBuilder) {

		float springHeight = 1.5f;
		float springTravel = 0.5f;

		float height = 0;
		if (forBuilder)
			height = (float) (1.5 / 2);

		// Setup mounts, shape
		/*
		 * BaseActor tmpActor = new BaseActor(ComponentNames.springJoint.name(),
		 * "temp_spring.png", world);
		 * 
		 * tmpActor.setPosition(0, 0); tmpActor.setSensor();
		 * 
		 * 
		 * ArrayList<Vector2> mounts = new ArrayList<Vector2>(); mounts.add(new
		 * Vector2(tmpActor.getCenter().x, tmpActor.getCenter().y - height));
		 * mounts.add(new Vector2(tmpActor.getCenter().x, tmpActor.getCenter().y
		 * + height)); tmpActor.destroy();
		 */

		BaseActor topFixture = new BaseActor(
				ComponentNames.SPRINGJOINT
						+ Assembler.NAME_SUBNAME_SPLIT
						+ ComponentSubNames.UPPER,
				"suspension_lower.png", gameState);

		ArrayList<Vector2> mountTop = new ArrayList<Vector2>();
		mountTop.add(topFixture.getCenter());
		topFixture.setMounts(mountTop, 0.0f);
		topFixture.setScaleY(1.2f);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.3f, 0.5f, new Vector2(0, -0.2f), 0);
		topFixture.setShapeBase(shape);
		// topFixture.setSensor();
		topFixture.setDensity(1);
		// topFixture.setScaleY(0.5f);
		//

		if (forBuilder) {
			topFixture.setPosition(0, height);
			/*
			 * FixtureDef fixtureDef = new FixtureDef(); PolygonShape shape =
			 * new PolygonShape(); shape.setAsBox(0.3f, 0.5f,new
			 * Vector2(0,-0.2f),0); fixtureDef.shape = shape; fixtureDef.density
			 * = 0; fixtureDef.isSensor = true;
			 * 
			 * topFixture.getPhysicsBody().createFixture(fixtureDef);
			 */
		}

		BaseActor botFixture = new BaseActor(
				ComponentNames.SPRINGJOINT
						+ Assembler.NAME_SUBNAME_SPLIT
						+ ComponentSubNames.LOWER,
				"suspension_upper.png", gameState);
		//
		ArrayList<Vector2> mountBot = new ArrayList<Vector2>();
		mountBot.add(botFixture.getCenter());
		botFixture.setMounts(mountBot, 0.0f);
		botFixture.setScaleY(1.5f);
		shape.setAsBox(0.3f, 0.5f, new Vector2(0, 0.2f), 0);
		botFixture.setShapeBase(shape);
		// botFixture.setScale(0.3f);
		// botFixture.setSensor();
		botFixture.setDensity(1);
		// botFixture.setScaleY(0.5f);

		if (forBuilder) {
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
			dJoint.frequencyHz = 10;
			dJoint.dampingRatio = 0.5f;
		}
		//
		gameState.getWorld().createJoint(dJoint);

		//
		PrismaticJointDef rJoint = new PrismaticJointDef();
		rJoint.initialize(topFixture.getPhysicsBody(),
				botFixture.getPhysicsBody(), botFixture.getCenter(),
				new Vector2(0, 1));
		rJoint.localAnchorA.set(topFixture.getCenter());
		rJoint.localAnchorB.set(botFixture.getCenter());
		rJoint.enableMotor = true;
		rJoint.collideConnected = false;
		if (!forBuilder) {
			rJoint.lowerTranslation = springHeight - springTravel;
			rJoint.upperTranslation = springHeight + springTravel;
			rJoint.enableLimit = true;
		} else {
			rJoint.lowerTranslation = springHeight;
			rJoint.upperTranslation = springHeight;
			rJoint.enableLimit = false;
		}

		// if(!forBuilder)
		gameState.getWorld().createJoint(rJoint);

		Component topComponent = new Component(topFixture,
				ComponentTypes.JOINT, ComponentNames.SPRINGJOINT);
		topComponent.setJointBodies(bodies);

		Component botComponent = new Component(botFixture,
				ComponentTypes.JOINT, ComponentNames.SPRINGJOINT);
		botComponent.setJointBodies(bodies);

		ArrayList<Component> retList = new ArrayList<Component>();
		retList.add(botComponent);
		retList.add(topComponent);

		return retList;
	}

	public static Component buildLife(GameState gameState, boolean forBuilder) {
		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(ComponentNames.LIFE,
				"life_small.png", gameState);

		tmpActor.setDensity(5);
		tmpActor.setScale(0.6f);

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(new Vector2(tmpActor.getCenter().x, tmpActor.getCenter().y
				- tmpActor.getHeight()));

		tmpActor.setMounts(mounts, tmpActor.getWidth() / 2);

		Component tmpComponent = new Component(tmpActor, ComponentTypes.PART,
				ComponentNames.LIFE);

		BaseActor cameraActor = new BaseActor(ComponentNames.LIFE,
				gameState);
		cameraActor.setMounts(mounts, tmpActor.getWidth() / 2);
		cameraActor.setDensity(1f);
		cameraActor.setScale(0.2f);
		cameraActor.setSensor();

		Component camerafocus = new Component(cameraActor, ComponentTypes.PART,
				ComponentNames.LIFE + Assembler.NAME_SUBNAME_SPLIT
						+ ComponentNames.CAMERAFOCUS);

		RopeJointDef dJoint = new RopeJointDef();
		dJoint.bodyA = tmpActor.getPhysicsBody();
		dJoint.bodyB = cameraActor.getPhysicsBody();
		dJoint.localAnchorA.set(tmpActor.getCenter());
		dJoint.localAnchorB.set(cameraActor.getCenter());
		dJoint.maxLength = 1f;

		/*DistanceJointDef dJoint = new DistanceJointDef();
		dJoint.initialize(tmpActor.getPhysicsBody(),
				cameraActor.getPhysicsBody(), tmpActor.getCenter(),
				cameraActor.getCenter());
		dJoint.length = 1f;
		dJoint.collideConnected = false;
		if (!forBuilder) {
			dJoint.frequencyHz = 10000;
			dJoint.dampingRatio = 1000f;
		}*/
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
			//rJoint.lowerTranslation = springHeight - springTravel;
			//rJoint.upperTranslation = springHeight + springTravel;
			rJoint.enableLimit = true;
		} else {
			//rJoint.lowerTranslation = springHeight;
			//rJoint.upperTranslation = springHeight;
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

	public static FixtureDef buildMount(Vector2 mount, boolean forBuilder) {
		FixtureDef fix = new FixtureDef();

		CircleShape shape = new CircleShape();
		shape.setRadius(0.1f);
		shape.setPosition(mount);
		fix.isSensor = true;
		fix.shape = shape;

		return fix;
	}
}
