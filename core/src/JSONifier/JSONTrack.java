package JSONifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;

public class JSONTrack extends JSONParentClass {

	// TODO: make it so its added to the json array
	enum TrackType implements Serializable {
		NORMAL;
	};

	private ArrayList<Vector2> points;
	private ArrayList<JSONComponent> componentList;
	private ArrayList<JSONJoint> componentJointList;
	private HashMap<String, Integer> componentJointTypes;
	private TrackType type = TrackType.NORMAL;

	public void applyOffset(Vector2 offset) {
		float newX = 0, newY = 0;

		for (Vector2 point : points) {
			point.x += offset.x;
			point.y += offset.y;
		}

		if(componentList == null) return;
		
		for (JSONComponent component : componentList) {
			HashMap<String, String> originalProperties = component
					.getProperties();
			Set<Entry<String, String>> props = originalProperties.entrySet();

			for (Entry<String, String> property : props) {
				if (property.getKey().compareTo(Properties.POSITION) == 0) {
					String[] values = property.getValue().split(",");
					newX = Float.parseFloat(values[0]) + offset.x;
					newY = Float.parseFloat(values[1]) + offset.y;
				}
			}

			originalProperties.put(Properties.POSITION,
					Properties.makePostionString(newX, newY));
			component.setProperties(originalProperties);

		}

	}

	public ArrayList<Vector2> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Vector2> points) {
		this.points = points;
	}

	public TrackType getType() {
		return type;
	}

	public void setType(TrackType type) {
		this.type = type;
	}

	@Override
	public ArrayList<JSONComponent> getComponentList() {
		return componentList;
	}

	public void setComponentList(ArrayList<JSONComponent> partList) {
		this.componentList = partList;
	}

	public String jsonify() {
		Json json = new Json();
		json.setIgnoreUnknownFields(true);
		return json.toJson(this);
	}

	public static JSONTrack objectify(String str) {
		Json json = new Json();
		json.setIgnoreUnknownFields(true);
		return json.fromJson(JSONTrack.class, str);
	}

	@Override
	public boolean equals(Object obj) {
		return (this.jsonify().compareTo(((JSONTrack) obj).jsonify()) == 0);
	}

	public String getId() {
		String ret = "";
		Iterator<Vector2> pointIter = points.iterator();
		while (pointIter.hasNext()) {
			Vector2 joint = pointIter.next();
			ret += joint.toString();
		}

		if (type == TrackType.NORMAL) {
			ret += TrackType.NORMAL.name();
		}

		return ret;
	}

	@Override
	public JSONParentType getParentType() {
		return JSONParentType.TRACK;
	}

	@Override
	public void setJointList(ArrayList<JSONJoint> joints) {
		componentJointList = joints;
	}
	
	@Override
	public ArrayList<JSONJoint> getJointList() {
		return componentJointList;
	}

	public HashMap<String, Integer> getComponentJointTypes() {
		return componentJointTypes;
	}

	public void setComponentJointTypes(HashMap<String, Integer> componentJointTypes) {
		this.componentJointTypes = componentJointTypes;
	}

	
	
}
