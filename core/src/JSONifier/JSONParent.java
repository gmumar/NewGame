package JSONifier;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;

public class JSONParent {

	private ArrayList<JSONJoint> jointList = null;
	private ArrayList<JSONComponent> componentList = null;
	
	public String jsonify(){
		Json obj = new Json();
		return obj.toJson(this);
	}
	
	public static JSONParent objectify(String str){
		Json json = new Json();
		return json.fromJson(JSONParent.class, str);
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
	
}
