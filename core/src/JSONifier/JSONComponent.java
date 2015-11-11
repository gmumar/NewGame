package JSONifier;

import java.util.HashMap;

import Component.Component;
import Component.ComponentLibrary;

import com.badlogic.gdx.utils.Json;

public class JSONComponent {

	private String componentName;
	private HashMap<String, String> properties;
	
	public String jsonify(){
		Json json = new Json();
		return json.toJson(this);
	}
	
	public static JSONComponent objectify(String str){
		
		Json json = new Json();
		JSONComponent comp = json.fromJson(JSONComponent.class, str);
		return comp;
		
	}
	
	public static Component toComponent(String str){
		JSONComponent json = objectify(str);
		Component ret = ComponentLibrary.getComponent(json.getComponentName());
		return ret;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}
	
}
