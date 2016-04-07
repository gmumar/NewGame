package Assembly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import Component.Component;
import JSONifier.JSONComponentName;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class AssemblyRules {

	private class SimpleJoint {
		Body bodyA;
		Body bodyB;
	}

	//return true if all checks pass
	public boolean checkBuild(World world, Body baseObject,
			ArrayList<Component> parts) {
		ArrayList<Body> partsConnectedToLife = getLifeConnectedParts(world,
				baseObject, parts);

		if (!checkDanglingParts(partsConnectedToLife, parts)) {
			return false;
		}
		
		return true;

	}

	public boolean checkDanglingParts(ArrayList<Body> connectedParts,
			ArrayList<Component> allParts) {
		// return true if all parts connected
		// even if one part missing return false
		Iterator<Component> allPartsIter = allParts.iterator();
		while (allPartsIter.hasNext()) {
			Body allPart = allPartsIter.next().getObject().getPhysicsBody();
			if (connectedParts.contains(allPart)) {
				;
			} else {

				return false;

			}
		}

		return true;

	}

	public ArrayList<Body> getLifeConnectedParts(World world, Body baseObject,
			ArrayList<Component> parts) {
		
		System.out.println("life connected parts");

		ArrayList<SimpleJoint> joints = new ArrayList<SimpleJoint>();

		Array<Contact> contacts = world.getContactList();
		Iterator<Contact> iter = contacts.iterator();
		while (iter.hasNext()) {
			SimpleJoint joint = null;
			Contact contact = iter.next();
			if (contact.isTouching()) {
				if (contact.getFixtureA().getUserData() != null
						&& contact.getFixtureB().getUserData() != null) {
					
					System.out.println(((JSONComponentName)contact.getFixtureA().getUserData()).getMountedId());
					
					if(((JSONComponentName)contact.getFixtureA().getUserData()).getMountedId().contains("*")){
						System.out.println("contining");
						continue;
					}
					
					System.out.println(((JSONComponentName)contact.getFixtureB().getUserData()).getMountedId());
					
					if(((JSONComponentName)contact.getFixtureB().getUserData()).getMountedId().contains("*")){	
						System.out.println("contining");
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

		/*//System.out.println("-------------------------");
		Iterator<Body> bodyIter = partsSeen.iterator();
		while (bodyIter.hasNext()) {
			Body obj = bodyIter.next();
			//System.out.println(obj.getUserData());
		}
		//System.out.println("-------------------------");*/

		return partsSeen;
	}

	private ArrayList<Body> getConnectedBodies(Body currentBody,
			ArrayList<SimpleJoint> joints) {

		Iterator<SimpleJoint> iter = joints.iterator();
		ArrayList<Body> connectedBodies = new ArrayList<Body>();

		// System.out.println(currentBody.getUserData());

		while (iter.hasNext()) {
			SimpleJoint joint = iter.next();

			//if (((String) joint.bodyA.getUserData())
			//		.compareTo((String) currentBody.getUserData()) == 0) {
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

			/*if (((String) joint.bodyB.getUserData())
					.compareTo((String) currentBody.getUserData()) == 0) {*/
			
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
