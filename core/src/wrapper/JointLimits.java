package wrapper;

import java.util.Iterator;

import Component.ComponentBuilder.ComponentNames;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef.JointType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class JointLimits {

	final static double TORQUE_FACTOR = 2e5;
	final static double FORCE_FACTOR = 7e3;
	final static double FORCE_DEVIDER = 5e5;

	final static int DEFAULT_BREAKING_TORQUE = (int) (3 * TORQUE_FACTOR);
	final static int DEFAULT_BREAKING_FORCE = (int) (25 * FORCE_FACTOR);

	final static int BAR_BAR_BREAKING_TORQUE = (int) (1 * TORQUE_FACTOR);
	final static int BAR_BAR_BREAKING_FORCE = (int) (20 * FORCE_FACTOR);

	final static int LIFE_BAR_BREAKING_TORQUE = (int) (1 * TORQUE_FACTOR);
	final static int LIFE_BAR_BREAKING_FORCE = (int) (1 * FORCE_FACTOR);

	public static void enableJointLimits(World world, float step) {

		Array<Joint> joints = new Array<Joint>();
		world.getJoints(joints);

		Iterator<Joint> iter = joints.iterator();
		while (iter.hasNext()) {
			Joint joint = iter.next();

			if (joint.getType() == JointType.PrismaticJoint) {
				continue;
			}

			float force = (float) ((joint.getReactionForce(1 / step).len2()) / FORCE_DEVIDER);
			float torque = joint.getReactionTorque(1 / step);

			if (jointBetween(joint, ComponentNames._AXLE_.name(),
					ComponentNames._TIRE_.name())) {
				continue;
			} 
			else if (jointHas(joint, ComponentNames._LIFE_.name())) {
				if (torque > LIFE_BAR_BREAKING_TORQUE) {
					System.out.println("break 2 torque " + torque);
					world.destroyJoint(joint);
					continue;
				}

				if (force > LIFE_BAR_BREAKING_FORCE) {
					world.destroyJoint(joint);
					System.out.println("break 2 force " + force);
					continue;
				}
			} else if (jointHas(joint, ComponentNames._BAR3_.name())) {
				if (torque > BAR_BAR_BREAKING_TORQUE) {
					world.destroyJoint(joint);
					System.out.println("break 1 torque " + torque);
					continue;
				}

				if (force > BAR_BAR_BREAKING_FORCE) {
					world.destroyJoint(joint);
					System.out.println("break 1 force " + force);
					continue;
				}
			}  else {
				if (torque > DEFAULT_BREAKING_TORQUE) {
					System.out.println("break default torque " + torque);
					world.destroyJoint(joint);
					continue;
				}

				if (force > DEFAULT_BREAKING_FORCE) {
					world.destroyJoint(joint);
					System.out.println("break default force " + force);
					continue;
				}
			}
			
			/*if (jointBetween(joint, ComponentNames._AXLE_.name(),
					ComponentNames._TIRE_.name())) {
				continue;
			} else if (jointBetween(joint, ComponentNames._BAR3_.name(),
					ComponentNames._BAR3_.name())) {
				if (torque > BAR_BAR_BREAKING_TORQUE) {
					world.destroyJoint(joint);
					System.out.println("break 1 torque " + torque);
					continue;
				}

				if (force > BAR_BAR_BREAKING_FORCE) {
					world.destroyJoint(joint);
					System.out.println("break 1 force " + force);
					continue;
				}
			} else if (jointBetween(joint, ComponentNames._LIFE_.name(),
					ComponentNames._BAR3_.name())) {
				if (torque > LIFE_BAR_BREAKING_TORQUE) {
					System.out.println("break 2 torque " + torque);
					world.destroyJoint(joint);
					continue;
				}

				if (force > LIFE_BAR_BREAKING_FORCE) {
					world.destroyJoint(joint);
					System.out.println("break 2 force " + force);
					continue;
				}
			} else {
				if (torque > DEFAULT_BREAKING_TORQUE) {
					System.out.println("break default torque " + torque);
					world.destroyJoint(joint);
					continue;
				}

				if (force > DEFAULT_BREAKING_FORCE) {
					world.destroyJoint(joint);
					System.out.println("break default force " + force);
					continue;
				}
			}*/
		}
	}

	private static boolean jointHas(Joint joint, String name1) {
		if (((String) joint.getBodyA().getUserData()).contains(name1)

		||

		((String) joint.getBodyB().getUserData()).contains(name1)) {
			return true;
		}

		return false;
	}

	private static boolean jointBetween(Joint joint, String name1, String name2) {
		if (((String) joint.getBodyA().getUserData()).contains(name1)
				&& ((String) joint.getBodyB().getUserData()).contains(name2)

				||

				((String) joint.getBodyA().getUserData()).contains(name1)
				&& ((String) joint.getBodyB().getUserData()).contains(name2)) {
			return true;
		}

		return false;
	}

}
