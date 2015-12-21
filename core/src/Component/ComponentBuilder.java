package Component;

import java.util.ArrayList;

import wrapper.BaseActor;
import Assembly.Assembler;
import Component.Component.ComponentTypes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class ComponentBuilder {

	public enum ComponentNames {
		_BAR3_, _TIRE_, _SOLIDJOINT_, _AXLE_, _SPRINGJOINT_, _WHEEL_, _LIFE_
	}

	public enum ComponentSubNames {
		_UPPER_, _LOWER_
	}

	public static Component buildComponent(String name, World world) {

		if (name.compareTo(ComponentNames._BAR3_.name()) == 0) {
			return buildBar3(world, false);
		} else if (name.compareTo(ComponentNames._SOLIDJOINT_.name()) == 0) {
			return buildSolidJoint(world, false);
		} else if (name.compareTo(ComponentNames._TIRE_.name()) == 0) {
			return buildTire(world, false);
		} else if (name.compareTo(ComponentNames._SPRINGJOINT_.name()) == 0) {
			return buildSpringJoint(world, false).get(0);
		} else if (name.compareTo(ComponentNames._LIFE_.name()) == 0) {
			return buildLife(world, false);
		}

		return null;
	}

	public static ArrayList<Component> buildJointComponent(String name,
			World world) {

		if (name.compareTo(ComponentNames._SPRINGJOINT_.name()) == 0) {
			return buildSpringJoint(world, false);
		}

		return null;
	}

	// Builders
	public static Component buildBar3(World world, boolean forBuilder) {
		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(ComponentNames._BAR3_.name(),
				"temp_bar.png", world);

		float mountHeight = tmpActor.getCenter().y;

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(new Vector2(
				tmpActor.getCenter().x - tmpActor.getWidth() / 2, mountHeight));
		mounts.add(new Vector2(tmpActor.getCenter().x, mountHeight));
		mounts.add(new Vector2(
				tmpActor.getCenter().x + tmpActor.getWidth() / 2, mountHeight));
		tmpActor.setMounts(mounts, tmpActor.getWidth() / 2);

		Component tmpComponent = new Component(tmpActor, ComponentTypes.PART,
				ComponentNames._BAR3_.name());

		if (forBuilder) {

		}

		return tmpComponent;
	}

	public static Component buildSolidJoint(World world, boolean forBuilder) {
		BaseActor tmpActor = new BaseActor(ComponentNames._SOLIDJOINT_.name(),
				"solid_joint.png", world);
		// tmpActor.disablePhysics();
		Component tmpComponent = new Component(tmpActor, ComponentTypes.JOINT,
				ComponentNames._SOLIDJOINT_.name());
		return tmpComponent;
	}

	public static Component buildAxle(World world, boolean forBuilder) {
		// Build axle
		BaseActor tmpActor = new BaseActor(ComponentNames._AXLE_.name(), world);
		Component tmpComponent = new Component(tmpActor, ComponentTypes.JOINT,
				ComponentNames._SOLIDJOINT_.name());
		return tmpComponent;
	}

	public static Component buildTire(World world, boolean forBuilder) {
		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(ComponentNames._WHEEL_.name(),
				"temp_tire_2.png", world);
		CircleShape shape = new CircleShape();

		tmpActor.setRestitution(0.9f);
		// tmpActor.setScale(1.2f);
		tmpActor.setPosition(0, 0);
		tmpActor.setDensity(40);
		shape.setRadius(tmpActor.getWidth() / 2);
		tmpActor.setShapeBase(shape);

		BaseActor fixture = new BaseActor(ComponentNames._AXLE_.name(), world);
		//fixture.setPosition(tmpActor.getCenter().x, tmpActor.getCenter().y);
		// fixture.setMounts(mounts, 0.0f);
		fixture.setScale(0.3f);
		fixture.setDensity(0);
		//fixture.setSensor();

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(fixture.getCenter());
		fixture.setMounts(mounts, 0.0f);

		RevoluteJointDef revJoint = new RevoluteJointDef();
		revJoint.initialize(fixture.getPhysicsBody(),
				tmpActor.getPhysicsBody(), fixture.getCenter());
		revJoint.collideConnected = false;
		revJoint.enableLimit = false;
		world.createJoint(revJoint);

		ArrayList<BaseActor> bodies = new ArrayList<BaseActor>();
		bodies.add(tmpActor);
		bodies.add(fixture);

		Component tireComponent = new Component(tmpActor, ComponentTypes.PART,
				ComponentNames._WHEEL_.name());
		tireComponent.setJointBodies(bodies);

		Component axleComponent = new Component(fixture, ComponentTypes.PART,
				ComponentNames._AXLE_.name());
		axleComponent.setJointBodies(bodies);

		return axleComponent;
	}

	public static ArrayList<Component> buildSpringJoint(World world,
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
				ComponentNames._SPRINGJOINT_.name()
						+ Assembler.NAME_SUBNAME_SPLIT
						+ ComponentSubNames._UPPER_.name(), world);
		
		ArrayList<Vector2> mountTop = new ArrayList<Vector2>();
		mountTop.add(topFixture.getCenter());
		topFixture.setMounts(mountTop, 0.0f);
		topFixture.setScale(0.3f);
		//topFixture.setSensor();
		topFixture.setDensity(1);
		// topFixture.setScaleY(0.5f);
		topFixture.setPosition(0, height);

		BaseActor botFixture = new BaseActor(
				ComponentNames._SPRINGJOINT_.name()
						+ Assembler.NAME_SUBNAME_SPLIT
						+ ComponentSubNames._LOWER_.name(), world);
		botFixture.setPosition(0, -height);
		ArrayList<Vector2> mountBot = new ArrayList<Vector2>();
		mountBot.add(botFixture.getCenter());
		botFixture.setMounts(mountBot, 0.0f);
		botFixture.setScale(0.3f);
		//botFixture.setSensor();
		botFixture.setDensity(1);
		// botFixture.setScaleY(0.5f);

		ArrayList<BaseActor> bodies = new ArrayList<BaseActor>();
		bodies.add(topFixture);
		bodies.add(botFixture);

		DistanceJointDef dJoint = new DistanceJointDef();
		dJoint.initialize(topFixture.getPhysicsBody(),
				botFixture.getPhysicsBody(), topFixture.getCenter(),
				botFixture.getCenter());
		dJoint.length = springHeight;
		dJoint.collideConnected = true;
		if (!forBuilder) {
			dJoint.frequencyHz = 10;
			dJoint.dampingRatio = 0.5f;
		}
		//
		world.createJoint(dJoint);

		//
		PrismaticJointDef rJoint = new PrismaticJointDef();
		rJoint.initialize(topFixture.getPhysicsBody(),
				botFixture.getPhysicsBody(), botFixture.getCenter(),
				new Vector2(0, 1));
		rJoint.localAnchorA.set(topFixture.getCenter());
		rJoint.localAnchorB.set(botFixture.getCenter());
		rJoint.enableMotor = true;
		rJoint.collideConnected = true;
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
		world.createJoint(rJoint);

		Component topComponent = new Component(topFixture,
				ComponentTypes.JOINT, ComponentNames._SPRINGJOINT_.name());
		topComponent.setJointBodies(bodies);

		Component botComponent = new Component(botFixture,
				ComponentTypes.JOINT, ComponentNames._SPRINGJOINT_.name());
		botComponent.setJointBodies(bodies);

		ArrayList<Component> retList = new ArrayList<Component>();
		retList.add(botComponent);
		retList.add(topComponent);

		return retList;
	}

	public static Component buildLife(World world, boolean forBuilder) {
		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(ComponentNames._LIFE_.name(),
				"life.png", world);

		tmpActor.setDensity(5);
		tmpActor.setScale(0.6f);

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(new Vector2(tmpActor.getCenter().x, tmpActor.getCenter().y
				- tmpActor.getHeight() / 2));

		tmpActor.setMounts(mounts, tmpActor.getWidth() / 2);

		Component tmpComponent = new Component(tmpActor, ComponentTypes.PART,
				ComponentNames._LIFE_.name());

		if (forBuilder) {
			//nothing
		}

		return tmpComponent;
	}

	public static FixtureDef buildMount(Vector2 mount, boolean forBuilder) {
		FixtureDef fix = new FixtureDef();

		CircleShape shape = new CircleShape();
		shape.setRadius(0.3f);
		shape.setPosition(mount);
		fix.isSensor = true;
		fix.shape = shape;

		return fix;
	}
}
