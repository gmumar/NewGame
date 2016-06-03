package Assembly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import Component.Component;
import Component.ComponentNames;
import JSONifier.JSONComponentName;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class AssemblyRules {

	public enum BuildErrors {
		BUILD_CLEAN, NOT_ENOUGH_BARS, TOO_MANY_BARS, NOT_ENOUGH_TIRES, TOO_MANY_TIRES, NOT_ENOUGH_SPRINGS, TOO_MANY_SPRINGS, DANGLING_PARTS
	}

	private class SimpleJoint {
		Body bodyA;
		Body bodyB;
	}

	// return true if all checks pass
	public BuildErrors checkBuild(World world, Body baseObject,
			ArrayList<Component> parts) {

		BuildErrors status = BuildErrors.BUILD_CLEAN;
		ArrayList<Body> partsConnectedToLife = getLifeConnectedParts(world,
				baseObject);

		status = checkDanglingParts(partsConnectedToLife, parts);

		if (status != BuildErrors.BUILD_CLEAN) {
			return status;
		}

		status = checkPartCounts(partsConnectedToLife);

		return status;

	}

	public BuildErrors checkPartCounts(ArrayList<Body> connectedParts) {

		// return true if all counts are within limits
		HashMap<String, Integer> counts = new HashMap<String, Integer>();

		for (Body part : connectedParts) {
			if (part.getUserData() == null)
				continue;

			JSONComponentName name = (JSONComponentName) part.getUserData();

			if (counts.containsKey(name.getBaseName())) {
				counts.put(name.getBaseName(),
						counts.get(name.getBaseName()) + 1);
			} else {
				counts.put(name.getBaseName(), 1);
			}

		}

		for (Entry<String, Integer> bla : counts.entrySet()) {
			System.out.println("AssemblyRules: " + bla.getKey() + " "
					+ bla.getValue());
		}

		if (!counts.containsKey(ComponentNames.BAR3)) {
			return BuildErrors.NOT_ENOUGH_BARS;
		}

		if (!counts.containsKey(ComponentNames.AXLE)) {
			return BuildErrors.NOT_ENOUGH_TIRES;
		}

		if (!counts.containsKey(ComponentNames.SPRINGJOINT)) {
			return BuildErrors.NOT_ENOUGH_SPRINGS;
		}

		if (counts.containsKey(ComponentNames.BAR3)
				&& counts.get(ComponentNames.BAR3) < 3) {
			return BuildErrors.NOT_ENOUGH_BARS;
		}

		if (counts.containsKey(ComponentNames.BAR3)
				&& counts.get(ComponentNames.BAR3) > 10) {
			return BuildErrors.TOO_MANY_BARS;
		}

		if (counts.containsKey(ComponentNames.AXLE)
				&& counts.get(ComponentNames.AXLE) < 2) {
			return BuildErrors.NOT_ENOUGH_TIRES;
		}

		if (counts.containsKey(ComponentNames.AXLE)
				&& counts.get(ComponentNames.AXLE) > 14) {
			return BuildErrors.TOO_MANY_TIRES;
		}

		if (counts.containsKey(ComponentNames.SPRINGJOINT)
		// && counts.get(ComponentNames.SPRINGJOINT) >= 2
				&& counts.get(ComponentNames.SPRINGJOINT) > 14) {

			return BuildErrors.TOO_MANY_SPRINGS;
		}

		return BuildErrors.BUILD_CLEAN;

	}

	public BuildErrors checkDanglingParts(ArrayList<Body> connectedParts,
			ArrayList<Component> allParts) {
		// return true if all parts connected
		// even if one part missing return false

		for (Component part : allParts) {
			Body allPart = part.getObject().getPhysicsBody();
			if (allPart.getUserData() == null)
				continue;
			if (connectedParts.contains(allPart)) {
				;
			} else {

				return BuildErrors.DANGLING_PARTS;

			}
		}

		return BuildErrors.BUILD_CLEAN;

	}

	public ArrayList<Body> getLifeConnectedParts(World world, Body baseObject) {

		ArrayList<SimpleJoint> joints = new ArrayList<SimpleJoint>();

		Array<Contact> contacts = world.getContactList();
		Iterator<Contact> iter = contacts.iterator();
		while (iter.hasNext()) {
			SimpleJoint joint = null;
			Contact contact = iter.next();
			if (contact.isTouching()) {
				if (contact.getFixtureA().getUserData() != null
						&& contact.getFixtureB().getUserData() != null) {

					if (((JSONComponentName) contact.getFixtureA()
							.getUserData()).getMountedId().contains("*")) {
						continue;
					}

					if (((JSONComponentName) contact.getFixtureB()
							.getUserData()).getMountedId().contains("*")) {
						continue;
					}

					joint = new SimpleJoint();
					joint.bodyA = contact.getFixtureA().getBody();
					joint.bodyB = contact.getFixtureB().getBody();
				}
			}
			if (joint != null)
				joints.add(joint);
		}

		ArrayList<Body> partsSeen = new ArrayList<Body>();

		Queue<Body> que = new ArrayBlockingQueue<Body>(100000);
		Body currentBody;

		que.add(baseObject);

		ArrayList<Body> connectedBodies;

		while (!que.isEmpty()) {
			currentBody = que.poll();
			if (partsSeen.contains(currentBody)) {
				continue;
			}
			partsSeen.add(currentBody);
			connectedBodies = getConnectedBodies(currentBody, joints);
			Iterator<Body> connectedBodyIter = connectedBodies.iterator();
			while (connectedBodyIter.hasNext()) {
				que.add(connectedBodyIter.next());
			}

		}

		/*
		 * //System.out.println("-------------------------"); Iterator<Body>
		 * bodyIter = partsSeen.iterator(); while (bodyIter.hasNext()) { Body
		 * obj = bodyIter.next(); //System.out.println(obj.getUserData()); }
		 * //System.out.println("-------------------------");
		 */

		return partsSeen;
	}

	private ArrayList<Body> getConnectedBodies(Body currentBody,
			ArrayList<SimpleJoint> joints) {

		Iterator<SimpleJoint> iter = joints.iterator();
		ArrayList<Body> connectedBodies = new ArrayList<Body>();

		// System.out.println(currentBody.getUserData());

		while (iter.hasNext()) {
			SimpleJoint joint = iter.next();

			// if (((String) joint.bodyA.getUserData())
			// .compareTo((String) currentBody.getUserData()) == 0) {
			if (((JSONComponentName) joint.bodyA.getUserData())
					.equals((JSONComponentName) currentBody.getUserData())) {

				connectedBodies.add(joint.bodyB);

				Iterator<JointEdge> bodyIter = joint.bodyB.getJointList()
						.iterator();
				while (bodyIter.hasNext()) {
					connectedBodies.add(bodyIter.next().other);
				}

				// System.out.println(joint.bodyB.getUserData());
				continue;
			}

			/*
			 * if (((String) joint.bodyB.getUserData()) .compareTo((String)
			 * currentBody.getUserData()) == 0) {
			 */

			if (((JSONComponentName) joint.bodyB.getUserData())
					.equals((JSONComponentName) currentBody.getUserData())) {
				connectedBodies.add(joint.bodyA);
				// System.out.println(joint.bodyA.getUserData());
				Iterator<JointEdge> bodyIter = joint.bodyA.getJointList()
						.iterator();
				while (bodyIter.hasNext()) {
					connectedBodies.add(bodyIter.next().other);
				}

				continue;
			}

		}

		return connectedBodies;

	}

}
