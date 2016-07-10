package wrapper;

import Component.ComponentNames;
import JSONifier.JSONComponentName;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef.JointType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class JointLimits {

	private final static double TORQUE_FACTOR = 8e4;
	private final static double FORCE_FACTOR = 1e4;
	//private final static double FORCE_DEVIDER = 1;//5e5;

	private final static int DEFAULT_BREAKING_TORQUE = (int) (3 * TORQUE_FACTOR);
	private final static int DEFAULT_BREAKING_FORCE = (int) (25 * FORCE_FACTOR);

	private final static int BAR_BAR_BREAKING_TORQUE = (int) (1 * TORQUE_FACTOR);
	private final static int BAR_BAR_BREAKING_FORCE = (int) (20 * FORCE_FACTOR);

	private final static int LIFE_BAR_BREAKING_TORQUE = (int) (1 * TORQUE_FACTOR);
	private final static int LIFE_BAR_BREAKING_FORCE = (int) (1 * FORCE_FACTOR);

	private static final Array<Joint> joints = new Array<Joint>();
	private static float force;
	private static float torque;
	private World world;

	private static String nameBodyA = "";
	private static String nameBodyB = "";

	public JointLimits(World world) {
		this.world = world;

	}

	final public void enableJointLimits(final float step) {

		world.getJoints(joints);
		// iter = joints.iterator();

		for (Joint joint : joints) {
			// joint = iter.next();

			//force = ((joint.getReactionForce(step).len2()) / FORCE_DEVIDER);
			force = joint.getReactionForce(step).len();
			torque = joint.getReactionTorque(step);

			if (torque < LIFE_BAR_BREAKING_TORQUE
					&& force < LIFE_BAR_BREAKING_FORCE) {
				continue;
			}

			nameBodyA = ((JSONComponentName) joint.getBodyA().getUserData())
					.getBaseName();
			nameBodyB = ((JSONComponentName) joint.getBodyB().getUserData())
					.getBaseName();

			if (nameBodyA.startsWith(ComponentNames.TRACK_NAME_PREFIX)
					|| nameBodyB.startsWith(ComponentNames.TRACK_NAME_PREFIX)) {
				continue;
			}

			if (((joint.getType() == JointType.PrismaticJoint) || jointBetween(
					ComponentNames.AXLE, ComponentNames.TIRE))) {
				continue;
			}

			if (jointHas(ComponentNames.LIFE)) {
				if (torque > LIFE_BAR_BREAKING_TORQUE) {
					world.destroyJoint(joint);
					continue;
				}

				if (force > LIFE_BAR_BREAKING_FORCE) {
					world.destroyJoint(joint);
					continue;
				}
			} else if (jointHas(ComponentNames.BAR3)) {
				if (torque > BAR_BAR_BREAKING_TORQUE) {
					world.destroyJoint(joint);
					continue;
				}

				if (force > BAR_BAR_BREAKING_FORCE) {
					world.destroyJoint(joint);
					continue;
				}
			} else {
				if (torque > DEFAULT_BREAKING_TORQUE) {
					world.destroyJoint(joint);
					continue;
				}

				if (force > DEFAULT_BREAKING_FORCE) {
					world.destroyJoint(joint);
					continue;
				}
			}

			/*
			 * if (jointBetween(joint, ComponentNames.AXLE,
			 * ComponentNames.TIRE)) { continue; } else if (jointBetween(joint,
			 * ComponentNames.BAR3, ComponentNames.BAR3)) { if (torque >
			 * BAR_BAR_BREAKING_TORQUE) { world.destroyJoint(joint); continue; }
			 * 
			 * if (force > BAR_BAR_BREAKING_FORCE) { world.destroyJoint(joint);
			 * continue; } } else if (jointBetween(joint, ComponentNames.LIFE,
			 * ComponentNames.BAR3)) { if (torque > LIFE_BAR_BREAKING_TORQUE) {
			 * 
			 * world.destroyJoint(joint); continue; }
			 * 
			 * if (force > LIFE_BAR_BREAKING_FORCE) { world.destroyJoint(joint);
			 * continue; } } else { if (torque > DEFAULT_BREAKING_TORQUE) {
			 * world.destroyJoint(joint); continue; }
			 * 
			 * if (force > DEFAULT_BREAKING_FORCE) { world.destroyJoint(joint);
			 * continue; } }
			 */
		}
	}

	final private static boolean jointHas(String name1) {
		/*
		 * if (((String) joint.getBodyA().getUserData()).contains(name1)
		 * 
		 * ||
		 * 
		 * ((String) joint.getBodyB().getUserData()).contains(name1)) { return
		 * true; }
		 */

		if (nameBodyA.compareTo(name1) == 0 || nameBodyB.compareTo(name1) == 0) {
			return true;
		}

		return false;
	}

	final private static boolean jointBetween(String name1, String name2) {
		/*
		 * if (((String) joint.getBodyA().getUserData()).contains(name1) &&
		 * ((String) joint.getBodyB().getUserData()).contains(name2)
		 * 
		 * ||
		 * 
		 * ((String) joint.getBodyA().getUserData()).contains(name1) &&
		 * ((String) joint.getBodyB().getUserData()).contains(name2)) { return
		 * true; }
		 */

		if (nameBodyA.compareTo(name1) == 0 && nameBodyB.compareTo(name2) == 0
				|| nameBodyA.compareTo(name2) == 0
				&& nameBodyB.compareTo(name1) == 0) {
			return true;
		}

		return false;
	}

}
