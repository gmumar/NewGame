package wrapper;

import java.util.Iterator;

import Component.ComponentNames;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef.JointType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class JointLimits {

	private final static double TORQUE_FACTOR = 1e5;
	private final static double FORCE_FACTOR = 1e4;
	private final static double FORCE_DEVIDER = 5e5;

	private final static int DEFAULT_BREAKING_TORQUE = (int) (3 * TORQUE_FACTOR);
	private final static int DEFAULT_BREAKING_FORCE = (int) (25 * FORCE_FACTOR);

	private final static int BAR_BAR_BREAKING_TORQUE = (int) (1 * TORQUE_FACTOR);
	private final static int BAR_BAR_BREAKING_FORCE = (int) (20 * FORCE_FACTOR);

	private final static int LIFE_BAR_BREAKING_TORQUE = (int) (1 * TORQUE_FACTOR);
	private final static int LIFE_BAR_BREAKING_FORCE = (int) (1 * FORCE_FACTOR);

	private static final Array<Joint> joints = new Array<Joint>();
	private static Iterator<Joint> iter;
	private static Joint joint;
	private static double force;
	private static double torque;
	private World world;

	public JointLimits(World world) {
		this.world = world;

	}

	final public void enableJointLimits(final float step) {

		world.getJoints(joints);
		iter = joints.iterator();

		while (iter.hasNext()) {
			joint = iter.next();

			if ((joint.getType() == JointType.PrismaticJoint)
					|| (jointBetween(joint, ComponentNames.AXLE,
							ComponentNames.TIRE))) {
				continue;
			}

			force = ((joint.getReactionForce(step).len2()) / FORCE_DEVIDER);
			torque = joint.getReactionTorque(step);

			if (jointHas(joint, ComponentNames.LIFE)) {
				if (torque > LIFE_BAR_BREAKING_TORQUE) {
					// System.out.println("break 2 torque " + torque);
					world.destroyJoint(joint);
					continue;
				}

				if (force > LIFE_BAR_BREAKING_FORCE) {
					world.destroyJoint(joint);
					// System.out.println("break 2 force " + force);
					continue;
				}
			} else if (jointHas(joint, ComponentNames.BAR3)) {
				if (torque > BAR_BAR_BREAKING_TORQUE) {
					world.destroyJoint(joint);
					// System.out.println("break 1 torque " + torque);
					continue;
				}

				if (force > BAR_BAR_BREAKING_FORCE) {
					world.destroyJoint(joint);
					// System.out.println("break 1 force " + force);
					continue;
				}
			} else {
				if (torque > DEFAULT_BREAKING_TORQUE) {
					// System.out.println("break default torque " + torque);
					world.destroyJoint(joint);
					continue;
				}

				if (force > DEFAULT_BREAKING_FORCE) {
					world.destroyJoint(joint);
					// System.out.println("break default force " + force);
					continue;
				}
			}

			/*
			 * if (jointBetween(joint, ComponentNames.AXLE,
			 * ComponentNames.TIRE)) { continue; } else if (jointBetween(joint,
			 * ComponentNames.BAR3, ComponentNames.BAR3)) { if (torque >
			 * BAR_BAR_BREAKING_TORQUE) { world.destroyJoint(joint);
			 * System.out.println("break 1 torque " + torque); continue; }
			 * 
			 * if (force > BAR_BAR_BREAKING_FORCE) { world.destroyJoint(joint);
			 * System.out.println("break 1 force " + force); continue; } } else
			 * if (jointBetween(joint, ComponentNames.LIFE,
			 * ComponentNames.BAR3)) { if (torque > LIFE_BAR_BREAKING_TORQUE) {
			 * System.out.println("break 2 torque " + torque);
			 * world.destroyJoint(joint); continue; }
			 * 
			 * if (force > LIFE_BAR_BREAKING_FORCE) { world.destroyJoint(joint);
			 * System.out.println("break 2 force " + force); continue; } } else
			 * { if (torque > DEFAULT_BREAKING_TORQUE) {
			 * System.out.println("break default torque " + torque);
			 * world.destroyJoint(joint); continue; }
			 * 
			 * if (force > DEFAULT_BREAKING_FORCE) { world.destroyJoint(joint);
			 * System.out.println("break default force " + force); continue; } }
			 */
		}
	}

	final private static boolean jointHas(Joint joint, String name1) {
		if (((String) joint.getBodyA().getUserData()).contains(name1)

		||

		((String) joint.getBodyB().getUserData()).contains(name1)) {
			return true;
		}

		return false;
	}

	final private static boolean jointBetween(Joint joint, String name1,
			String name2) {
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
