package JSONifier;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.utils.Json;

public class JSONCar {

	private ArrayList<JSONJoint> jointList = null;
	private ArrayList<JSONComponent> componentList = null;
	
	public String jsonify(){
		Json obj = new Json();
		obj.setIgnoreUnknownFields(true);
		return obj.toJson(this);
	}
	
	public static JSONCar objectify(String str){
		Json json = new Json();
		json.setIgnoreUnknownFields(true);
		return json.fromJson(JSONCar.class, str);
	}
	
	public ArrayList<JSONJoint> getJointList() {
		return jointList;
	}
	
	public ArrayList<JSONComponent> getComponentList() {
		return componentList;
	}

	public void setComponentList(ArrayList<JSONComponent> parts) {
		componentList = parts;
	}
	
	public void setJointList(ArrayList<JSONJoint> joints) {
		jointList = joints;
	}

	@Override
	public boolean equals(Object obj) {
		return (this.jsonify().compareTo(((JSONCar)obj).jsonify())==0);
	}
	
	public String getId(){
		String ret = "";
		Iterator<JSONJoint> jointIter = jointList.iterator();
		while(jointIter.hasNext()){
			JSONJoint joint = jointIter.next();
			ret += joint.jsonify();
		}
		
		Iterator<JSONComponent> componentIter = componentList.iterator();
		while(componentIter.hasNext()){
			JSONComponent component = componentIter.next();
			ret += component.jsonify();
		}
		
		return ret;
	}
	
}
