package JSONifier;

import java.util.HashMap;

import com.badlogic.gdx.utils.Json;

public class JSONJoint {

		//public String m1;
		//public String m2;
		public JSONComponentName m1;
		public JSONComponentName m2;
		public HashMap<String, String> props;
		
		public String jsonify(){
			Json json = new Json();
			json.setIgnoreUnknownFields(true);
			return json.toJson(this);
		}
		
		public static JSONJoint objectify(String str){
			
			Json json = new Json();
			json.setIgnoreUnknownFields(true);
			JSONJoint comp = json.fromJson(JSONJoint.class, str);
			return comp;
			
		}

		/*public String getMount1() {
			return m1;
		}

		public void setMount1(String mount1) {
			this.m1 = mount1;
		}

		public String getMount2() {
			return m2;
		}

		public void setMount2(String mount2) {
			this.m2 = mount2;
		}*/

		public HashMap<String, String> getProperties() {
			return props;
		}

		public JSONComponentName getMount1() {
			return m1;
		}

		public void setMount1(JSONComponentName m1) {
			this.m1 = m1;
		}

		public JSONComponentName getMount2() {
			return m2;
		}

		public void setMount2(JSONComponentName m2) {
			this.m2 = m2;
		}

		public void setProperties(HashMap<String, String> properties) {
			this.props = properties;
		}
		
		
}
