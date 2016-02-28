package JSONifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import wrapper.BaseActor;
import wrapper.GamePreferences;
import Component.Component;
import Component.Component.ComponentTypes;
import GroundWorks.GroundUnitDescriptor;
import JSONifier.JSONTrack.TrackType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class JSONCompiler {

	public String compile(World world, ArrayList<Component> parts, HashMap<String, Integer> jointTypes) {

		JSONCar car = new JSONCar();
		Preferences prefs = Gdx.app
				.getPreferences(GamePreferences.CAR_PREF_STR);

		// component list
		ArrayList<JSONComponent> JSONparts = new ArrayList<JSONComponent>();
		Iterator<Component> partsIter = parts.iterator();
		while (partsIter.hasNext()) {
			Component part = partsIter.next();
			if (part.getComponentTypes() == ComponentTypes.PART) {
				//JSONparts.add(part.toJSONComponent((String) part.getObject()
				//		.getPhysicsBody().getUserData()));
				JSONparts.add(part.toJSONComponent(part.getjComponentName()));

			} else if (part.getComponentTypes() == ComponentTypes.JOINT) {
				ArrayList<BaseActor> bodies = part.getJointBodies();
				BaseActor body = bodies.get(0);
				//String[] nameParts =  ((String) body.getPhysicsBody().getUserData())
				//		.split(Assembler.NAME_SUBNAME_SPLIT);
				//String bodyName = nameParts[0];
				//String bodyIndex = nameParts[1].split(Assembler.NAME_ID_SPLIT)[1];
				//JSONparts.add(part.toJSONComponent(bodyName + Assembler.NAME_ID_SPLIT + bodyIndex, body));
				JSONparts.add(part.toJSONComponent(part.getjComponentName(), body));
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
					/*joint.setMount1((String) contact.getFixtureA()
							.getUserData());
					joint.setMount2((String) contact.getFixtureB()
							.getUserData());*/
					
					System.out.println("JSONCompiler fixtureA:" + (JSONComponentName) contact.getFixtureA()
							.getUserData() );
					System.out.println("JSONCompiler fixtureB:" + (JSONComponentName) contact.getFixtureB()
							.getUserData() );
					
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
		
		car.setJointTypeList(jointTypes);
		
		prefs.putString(GamePreferences.CAR_MAP_STR, car.jsonify());
		prefs.flush();

		System.out.println("JSONCompiler :car str " + prefs
				.getString(GamePreferences.CAR_MAP_STR, "Error"));
		
		return car.jsonify();
	}
	
	public void compile(ArrayList<GroundUnitDescriptor> mapList){
		Preferences prefs = Gdx.app
				.getPreferences(GamePreferences.CAR_PREF_STR);
		
		Iterator<GroundUnitDescriptor> iter = mapList.iterator();
		ArrayList<Vector2> trackArray = new ArrayList<Vector2>();
		
		while(iter.hasNext()){
			GroundUnitDescriptor unit = iter.next();
			
			trackArray.add(unit.getStart());
			
		}
		
		JSONTrack track = new JSONTrack();
		track.setPoints(trackArray);
		track.setType(TrackType.NORMAL);
		
		prefs.putString(GamePreferences.TRACK_MAP_STR, track.jsonify());
		prefs.flush();
		
		//System.out.println(prefs
			//	.getString(GamePreferences.TRACK_MAP_STR, "Error"));
		
	}
}
