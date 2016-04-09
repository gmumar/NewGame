package JSONifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import wrapper.Globals;
import Component.Component;
import Component.Component.ComponentTypes;
import Component.ComponentProperties;
import GroundWorks.GroundUnitDescriptor;
import JSONifier.JSONTrack.TrackType;
import User.User;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class JSONCompiler {

	public String compile(World world, ArrayList<Component> parts,
			HashMap<String, Integer> jointTypes) {

		JSONCar car = new JSONCar();
		/*
		 * Preferences prefs = Gdx.app
		 * .getPreferences(GamePreferences.CAR_PREF_STR);
		 */

		// component list
		ArrayList<JSONComponent> JSONparts = new ArrayList<JSONComponent>();
		JSONComponent JSONPart;
		
		//AdditionalProperties
		ArrayList<JSONComponent> addParts = new ArrayList<JSONComponent>();

		for (Component part : parts) {
			if (part.getComponentTypes() == ComponentTypes.PART
					|| part.getComponentTypes() == ComponentTypes.JOINT) {
				// JSONparts.add(part.toJSONComponent((String) part.getObject()
				// .getPhysicsBody().getUserData()));
				JSONPart = part.toJSONComponent(part.getjComponentName());
				
				if (part.getjComponentName().getLevel() == Globals.DISABLE_LEVEL) {
					addParts.add(JSONPart);
				} else {
					System.out.println("JSONCompiler: "
							+ part.getjComponentName());
					JSONparts.add(JSONPart);
				}

			}/*
			 * else if (part.getComponentTypes() == ComponentTypes.JOINT) {
			 * ArrayList<BaseActor> bodies = part.getJointBodies(); BaseActor
			 * body = bodies.get(0); //String[] nameParts = ((String)
			 * body.getPhysicsBody().getUserData()) //
			 * .split(Assembler.NAME_SUBNAME_SPLIT); //String bodyName =
			 * nameParts[0]; //String bodyIndex =
			 * nameParts[1].split(Assembler.NAME_ID_SPLIT)[1];
			 * //JSONparts.add(part.toJSONComponent(bodyName +
			 * Assembler.NAME_ID_SPLIT + bodyIndex, body));
			 * JSONparts.add(part.toJSONComponent(part.getjComponentName(),
			 * body)); }
			 */
		}
		
		if(!addParts.isEmpty()){
			car.setAddComponents(addParts);
		}
		car.setComponentList(JSONparts);

		// joint list
		ArrayList<JSONJoint> joints = new ArrayList<JSONJoint>();

		Array<Contact> contacts = world.getContactList();
		Iterator<Contact> iter = contacts.iterator();
		while (iter.hasNext()) {
			JSONJoint joint = null;
			Contact contact = iter.next();
			if (contact.isTouching()) {
				if (contact.getFixtureA().getUserData() != null
						&& contact.getFixtureB().getUserData() != null) {

					joint = new JSONJoint();
					/*
					 * joint.setMount1((String) contact.getFixtureA()
					 * .getUserData()); joint.setMount2((String)
					 * contact.getFixtureB() .getUserData());
					 */

					System.out.println("JSONCompiler fixtureA:"
							+ (JSONComponentName) contact.getFixtureA()
									.getUserData());
					System.out.println("JSONCompiler fixtureB:"
							+ (JSONComponentName) contact.getFixtureB()
									.getUserData());

					joint.setMount1((JSONComponentName) contact.getFixtureA()
							.getUserData());
					joint.setMount2((JSONComponentName) contact.getFixtureB()
							.getUserData());

				}
			}
			if (joint != null)
				joints.add(joint);
		}
		car.setJointList(joints);

		HashMap<String, Integer> shortJointTypes = new HashMap<String, Integer>();
		Set<Entry<String, Integer>> jointTypesIter = jointTypes.entrySet();

		for (Entry<String, Integer> jointType : jointTypesIter) {
			if (jointType.getValue() == Globals.ROTATABLE_JOINT) {
				shortJointTypes.put(jointType.getKey(), jointType.getValue());
			}
		}

		car.setJointTypeList(shortJointTypes);

		/*
		 * prefs.putString(GamePreferences.CAR_MAP_STR, car.jsonify());
		 * prefs.flush();
		 * 
		 * System.out.println("JSONCompiler :car str " +
		 * prefs.getString(GamePreferences.CAR_MAP_STR, "Error"));
		 */

		return car.jsonify();
	}

	public String compile(World world, ArrayList<GroundUnitDescriptor> mapList,
			ArrayList<Component> parts, HashMap<String, Integer> jointTypes) {
		JSONTrack track = new JSONTrack();

		// Preferences prefs = Gdx.app
		// .getPreferences(GamePreferences.CAR_PREF_STR);

		// component list
		ArrayList<JSONComponent> JSONparts = new ArrayList<JSONComponent>();

		for (Component part : parts) {
			if (part.getComponentTypes() == ComponentTypes.PART
					|| part.getComponentTypes() == ComponentTypes.JOINT) {
				JSONparts.add(part.toJSONComponent(part.getjComponentName()));

			}
		}
		track.setComponentList(JSONparts);

		// joint list
		ArrayList<JSONJoint> joints = new ArrayList<JSONJoint>();

		Array<Contact> contacts = world.getContactList();
		Iterator<Contact> contactIter = contacts.iterator();
		while (contactIter.hasNext()) {
			JSONJoint joint = null;
			Contact contact = contactIter.next();
			if (contact.isTouching()) {
				if (contact.getFixtureA().getUserData() != null
						&& contact.getFixtureB().getUserData() != null) {

					joint = new JSONJoint();
					/*
					 * joint.setMount1((String) contact.getFixtureA()
					 * .getUserData()); joint.setMount2((String)
					 * contact.getFixtureB() .getUserData());
					 */

					System.out.println("JSONCompiler fixtureA:"
							+ (JSONComponentName) contact.getFixtureA()
									.getUserData());
					System.out.println("JSONCompiler fixtureB:"
							+ (JSONComponentName) contact.getFixtureB()
									.getUserData());

					joint.setMount1((JSONComponentName) contact.getFixtureA()
							.getUserData());
					joint.setMount2((JSONComponentName) contact.getFixtureB()
							.getUserData());

				}
			}
			if (joint != null)
				joints.add(joint);
		}
		track.setJointList(joints);

		track.setComponentJointTypes(jointTypes);

		Iterator<GroundUnitDescriptor> iter = mapList.iterator();
		ArrayList<Vector2> trackArray = new ArrayList<Vector2>();

		while (iter.hasNext()) {
			GroundUnitDescriptor unit = iter.next();

			trackArray.add(unit.getStart());

		}

		track.setPoints(trackArray);
		track.setType(TrackType.NORMAL);

		User.getInstance().setCurrentTrack(track.jsonify());

		// prefs.putString(GamePreferences.TRACK_MAP_STR, track.jsonify());
		// prefs.flush();

		System.out.println(track.jsonify());

		return track.jsonify();

	}
}
