package JSONifier;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Json;

public class JSONComponent {

	//private String cN;//componentName
	private JSONComponentName cN;
	private Map<String, String> props;

	public String jsonify() {
		Json json = new Json();
		json.setIgnoreUnknownFields(true);
		return json.toJson(this);
	}

	public static JSONComponent objectify(String str) {

		Json json = new Json();
		json.setIgnoreUnknownFields(true);
		JSONComponent comp = json.fromJson(JSONComponent.class, str);
		return comp;

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
	
	public Map<String, String> getProperties() {
		return props;
	}

	public void setProperties(Map<String, String> properties) {
		this.props = properties;
	}

}
