package JSONifier;

import Component.ComponentNames;
import Component.ComponentProperties;

import com.google.gson.Gson;

public class JSONComponent {

	//private String cN;//componentName
	private JSONComponentName cN;
	private ComponentProperties props;
	
	public String jsonify() {
		Gson json = new Gson();
		//json.setIgnoreUnknownFields(true);
		return json.toJson(this);
	}

	public static JSONComponent objectify(String str) {

		Gson json = new Gson();
		//json.setIgnoreUnknownFields(true);
		return json.fromJson(str, JSONComponent.class);

	}

	/*public String getComponentName() {
		return cN;
	}

	public void setComponentName(String componentName) {
		this.cN = componentName;
	}*/
	
	public JSONComponentName getjComponentName() {
		return cN;
	}

	public void setjComponentName(JSONComponentName cN) {
		this.cN = cN;
	}
	
	public String getBaseName() {
		return cN.getBaseName();
	}
	
	public String getSubName() {
		return cN.getSubName();
	}
	
	public String getComponentId() {
		return cN.getComponentId();
	}
	
	public String getMountId() {
		return cN.getMountId();
	}

	public ComponentProperties getProperties() {
		return props;
	}

	public void setProperties(ComponentProperties props) {
		this.props = props;
	}
	
	//public Map<String, String> getProperties() {
	//	return props;
	//}
	
	/*public void setProperties(Map<String, String> properties) {
		this.props = properties;
	}*/
	
	static public Integer getBaseNameIndex(String componentName){
		if(componentName.compareTo(ComponentNames.BAR3)==0){
			return 0;
		} else if (componentName.compareTo(ComponentNames.SPRINGJOINT)==0){
			return 1;
		} else if (componentName.compareTo(ComponentNames.TIRE)==0 || 
				componentName.compareTo(ComponentNames.AXLE)==0){
			return 2;
		}
		
		return -1;
	}

}
