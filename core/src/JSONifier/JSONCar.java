package JSONifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;

public class JSONCar extends JSONParentClass {

	private ArrayList<JSONJoint> jointList = null;
	private ArrayList<JSONComponent> componentList = null;
	private Map<String, Integer> jointTypeList = null;
	private ArrayList<JSONComponent> addComponents = null;

	public String jsonify() {
		Gson obj = new Gson();
		//obj.setIgnoreUnknownFields(true);
		return obj.toJson(this);
	}

	public static JSONCar objectify(String data) {
		Gson json = new Gson();
		//json.setIgnoreUnknownFields(true);
		return json.fromJson(data, JSONCar.class);
	}

	public ArrayList<JSONComponent> getAddComponents() {
		return addComponents;
	}

	public void setAddComponents(ArrayList<JSONComponent> addComponents) {
		this.addComponents = addComponents;
	}

	public ArrayList<JSONJoint> getJointList() {
		return jointList;
	}

	@Override
	public ArrayList<JSONComponent> getComponentList() {
		return componentList;
	}

	public void setComponentList(ArrayList<JSONComponent> parts) {
		componentList = parts;
	}

	public void setJointList(ArrayList<JSONJoint> joints) {
		jointList = joints;
	}

	public Map<String, Integer> getJointTypeList() {
		return jointTypeList;
	}

	public void setJointTypeList(Map<String, Integer> jointTypeList) {
		this.jointTypeList = jointTypeList;
	}

	@Override
	public boolean equals(Object obj) {

		//JsonElement o1 = parser.parse(this.jsonify());
		//JsonElement o2 = parser.parse(((JSONCar) obj).jsonify());
		
		JSONCar otherCar = ((JSONCar) obj);
		return ((this.getItemIndex() == otherCar.getItemIndex()));

		//String id1 = getObjectId();
		//String id2 = ((JSONCar) obj).getObjectId();
		
		//return id1.equals(id2);
	}

	public String getId() {
		String ret = "";
		Iterator<JSONJoint> jointIter = jointList.iterator();
		while (jointIter.hasNext()) {
			JSONJoint joint = jointIter.next();
			ret += joint.jsonify();
		}

		Iterator<JSONComponent> componentIter = componentList.iterator();
		while (componentIter.hasNext()) {
			JSONComponent component = componentIter.next();
			ret += component.jsonify();
		}

		return ret;
	}

	@Override
	public JSONParentType getParentType() {
		return JSONParentType.CAR;
	}

}
