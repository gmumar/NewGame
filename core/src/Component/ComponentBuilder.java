package Component;

import java.util.ArrayList;

import wrapper.BaseActor;
import Component.Component.ComponentTypes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class ComponentBuilder {

	public enum ComponentNames {
		bar3, tire, solidJoint, axle
	}

	public static Component buildComponent(String name, World world) {

		if (name.compareTo(ComponentNames.bar3.name()) == 0) {
			return buildBar3(world);
		} else if (name.compareTo(ComponentNames.solidJoint.name()) == 0) {
			return buildSolidJoint(world);
		} else if (name.compareTo(ComponentNames.tire.name()) == 0) {
			return buildTire(world);
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
		tmpActor.setDensity(170f);
		shape.setRadius(tmpActor.getWidth() / 2);
		tmpActor.setShapeBase(shape);

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(tmpActor.getCenter());
		tmpActor.setMounts(mounts, 0.0f);

		Component tmpComponent = new Component(tmpActor, ComponentTypes.PART,
				ComponentNames.tire.name());
		return tmpComponent;
	}
	
	public static FixtureDef buildMount(Vector2 mount){
		FixtureDef fix = new FixtureDef();
		
		CircleShape shape = new CircleShape();
		shape.setRadius(0.3f);
		shape.setPosition(mount);
		fix.isSensor = true;
		fix.shape = shape;
		
		
		return fix;
	}

}
