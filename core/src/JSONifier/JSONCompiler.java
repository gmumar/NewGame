package JSONifier;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.BaseActor;
import wrapper.GamePreferences;
import Assembly.Assembler;
import Component.Component;
import Component.Component.ComponentTypes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class JSONCompiler {

	public void compile(World world, ArrayList<Component> parts) {

		JSONParent car = new JSONParent();
		Preferences prefs = Gdx.app
				.getPreferences(GamePreferences.CAR_PREF_STR);

		// component list
		ArrayList<JSONComponent> JSONparts = new ArrayList<JSONComponent>();
		Iterator<Component> partsIter = parts.iterator();
		while (partsIter.hasNext()) {
			Component part = partsIter.next();
			if (part.getComponentTypes() == ComponentTypes.PART) {
				JSONparts.add(part.toJSONComponent((String) part.getObject()
						.getPhysicsBody().getUserData()));

			} else if (part.getComponentTypes() == ComponentTypes.JOINT) {
				ArrayList<BaseActor> bodies = part.getJointBodies();
				BaseActor body = bodies.get(0);
				String[] nameParts =  ((String) body.getPhysicsBody().getUserData())
						.split(Assembler.NAME_SUBNAME_SPLIT);
				String bodyName = nameParts[0];
				String bodyIndex = nameParts[1].split(Assembler.NAME_ID_SPLIT)[1];
				JSONparts.add(part.toJSONComponent(bodyName + Assembler.NAME_ID_SPLIT + bodyIndex, body));
			}
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
					joint.setMount1((String) contact.getFixtureA()
							.getUserData());
					joint.setMount2((String) contact.getFixtureB()
							.getUserData());
					
				}
			}
			if (joint != null)
				joints.add(joint);
		}
		car.setJointList(joints);

		prefs.putString(GamePreferences.CAR_MAP_STR, car.jsonify());
		prefs.flush();

		System.out.println(prefs
				.getString(GamePreferences.CAR_MAP_STR, "Error"));
	}
}
