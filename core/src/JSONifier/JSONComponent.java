package JSONifier;

import java.util.HashMap;

import com.badlogic.gdx.utils.Json;

public class JSONComponent {

	private String cN;//componentName
	private HashMap<String, String> props;

	public String jsonify() {
		Json json = new Json();
		return json.toJson(this);
	}

	public static JSONComponent objectify(String str) {

		Json json = new Json();
		JSONComponent comp = json.fromJson(JSONComponent.class, str);
		return comp;

	}

	public String getComponentName() {
		return cN;
	}

	public void setComponentName(String componentName) {
		this.cN = componentName;
	}

	public HashMap<String, String> getProperties() {
		return props;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.props = properties;
	}

}
