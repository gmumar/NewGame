package JSONifier;

import java.util.HashMap;

import com.badlogic.gdx.utils.Json;

public class JSONJoint {

		public String mount1;
		public String mount2;
		public HashMap<String, String> properties;
		
		public String jsonify(){
			Json json = new Json();
			return json.toJson(this);
		}
		
		public static JSONJoint objectify(String str){
			
			Json json = new Json();
			JSONJoint comp = json.fromJson(JSONJoint.class, str);
			return comp;
			
		}

		public String getMount1() {
			return mount1;
		}

		public void setMount1(String mount1) {
			this.mount1 = mount1;
		}

		public String getMount2() {
			return mount2;
		}

		public void setMount2(String mount2) {
			this.mount2 = mount2;
		}

		public HashMap<String, String> getProperties() {
			return properties;
		}

		public void setProperties(HashMap<String, String> properties) {
			this.properties = properties;
		}
		
		
}
