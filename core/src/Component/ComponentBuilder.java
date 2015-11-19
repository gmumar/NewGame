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

public class ComponentBuilder {

	public enum ComponentNames {
		bar3, tire, solidJoint, axle, springJoint
	}

	public enum ComponentSubNames {
		upper, lower
	}

	public static Component buildComponent(String name, World world) {

		if (name.compareTo(ComponentNames.bar3.name()) == 0) {
			return buildBar3(world);
		} else if (name.compareTo(ComponentNames.solidJoint.name()) == 0) {
			return buildSolidJoint(world);
		} else if (name.compareTo(ComponentNames.tire.name()) == 0) {
			return buildTire(world);
		} else if (name.compareTo(ComponentNames.springJoint.name()) == 0) {
			return buildSpringJoint(world);
		}

		return null;
	}

	// Builders
	public static Component buildBar3(World world) {
		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(ComponentNames.bar3.name(),
				"bar3.png", world);

		float mountHeight = tmpActor.getCenter().y;

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(new Vector2(
				tmpActor.getCenter().x - tmpActor.getWidth() / 2, mountHeight));
		mounts.add(new Vector2(tmpActor.getCenter().x, mountHeight));
		mounts.add(new Vector2(
				tmpActor.getCenter().x + tmpActor.getWidth() / 2, mountHeight));
		tmpActor.setMounts(mounts, tmpActor.getWidth() / 2);

		Component tmpComponent = new Component(tmpActor, ComponentTypes.PART,
				ComponentNames.bar3.name());
		return tmpComponent;
	}

	public static Component buildSolidJoint(World world) {
		BaseActor tmpActor = new BaseActor(ComponentNames.solidJoint.name(),
				"solid_joint.png", world);
		// tmpActor.disablePhysics();
		Component tmpComponent = new Component(tmpActor, ComponentTypes.JOINT,
				ComponentNames.solidJoint.name());
		return tmpComponent;
	}

	public static Component buildAxle(World world) {
		// Build axle
		BaseActor tmpActor = new BaseActor(ComponentNames.axle.name(), world);
		Component tmpComponent = new Component(tmpActor, ComponentTypes.JOINT,
				ComponentNames.solidJoint.name());
		return tmpComponent;
	}

	public static Component buildTire(World world) {
		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(ComponentNames.tire.name(),
				"temp_tire.png", world);
		CircleShape shape = new CircleShape();

		// tmpActor.setRestitution(0.9f);
		tmpActor.setScale(1f);
		tmpActor.setDensity(40);
		shape.setRadius(tmpActor.getWidth() / 2);
		tmpActor.setShapeBase(shape);

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(tmpActor.getCenter());
		tmpActor.setMounts(mounts, 0.0f);

		Component tmpComponent = new Component(tmpActor, ComponentTypes.PART,
				ComponentNames.tire.name());
		return tmpComponent;
	}

	public static Component buildSpringJoint(World world) {
		// Setup mounts, shape
		BaseActor tmpActor = new BaseActor(ComponentNames.springJoint.name(),
				"temp_spring.png", world);

		// tmpActor.setDensity(40);
		tmpActor.setSensor();

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(new Vector2(tmpActor.getCenter().x, tmpActor.getCenter().y
				- tmpActor.getHeight() / 2));
		mounts.add(new Vector2(tmpActor.getCenter().x, tmpActor.getCenter().y
				+ tmpActor.getHeight() / 2));
		tmpActor.setMounts(mounts, 0.0f);
		tmpActor.destroy();

		BaseActor topFixture = new BaseActor(
				ComponentNames.springJoint.name()
						+ Assembler.NAME_SUBNAME_SPLIT
						+ ComponentSubNames.upper.name(), "solid_joint.png",
				world);
		topFixture.setPosition(mounts.get(0).x, mounts.get(0).y);
		ArrayList<Vector2> mountTop = new ArrayList<Vector2>();
		mountTop.add(mounts.get(0));
		topFixture.setMounts(mountTop, 0.0f);
		topFixture.setSensor();

		BaseActor botFixture = new BaseActor(
				ComponentNames.springJoint.name()
						+ Assembler.NAME_SUBNAME_SPLIT
						+ ComponentSubNames.lower.name(), "solid_joint.png",
				world);
		botFixture.setPosition(mounts.get(1).x, mounts.get(1).y);
		ArrayList<Vector2> mountBot = new ArrayList<Vector2>();
		mountTop.add(mounts.get(1));
		botFixture.setMounts(mountBot, 0.0f);
		botFixture.setSensor();

		ArrayList<BaseActor> bodies = new ArrayList<BaseActor>();
		bodies.add(topFixture);
		bodies.add(botFixture);

		DistanceJointDef dJoint = new DistanceJointDef();
		dJoint.initialize(topFixture.getPhysicsBody(),
				botFixture.getPhysicsBody(), mounts.get(0), mounts.get(1));
		world.createJoint(dJoint);

		Component tmpComponent = new Component(topFixture,
				ComponentTypes.JOINT, ComponentNames.springJoint.name());
		tmpComponent.setJointBodies(bodies);

		return tmpComponent;
	}

	public static FixtureDef buildMount(Vector2 mount) {
		FixtureDef fix = new FixtureDef();

		CircleShape shape = new CircleShape();
		shape.setRadius(0.3f);
		shape.setPosition(mount);
		fix.isSensor = true;
		fix.shape = shape;

		return fix;
	}

}
