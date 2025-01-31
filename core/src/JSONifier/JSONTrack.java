package JSONifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import Component.ComponentProperties;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.Gson;

public class JSONTrack extends JSONParentClass {

	public enum TrackType implements Serializable {
		FORREST, ARTIC;
	};

	private ArrayList<Vector2> points = null;
	private ArrayList<JSONComponent> componentList = null;
	private ArrayList<JSONJoint> componentJointList = null;
	private HashMap<String, Integer> componentJointTypes = null;
	private TrackType type = TrackType.FORREST;
	private ArrayList<Float> smallPoints = null;
	private float bestTime = 0.0f;
	private int difficulty = 0;

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public float getBestTime() {
		return bestTime;
	}

	public void setBestTime(float bestTime) {
		this.bestTime = bestTime;
	}

	public ArrayList<Float> getSmallPoints() {
		return smallPoints;
	}

	public void setSmallPoints(ArrayList<Float> smallPoints) {
		this.smallPoints = smallPoints;
		this.points = null;
	}

	public void applyOffset(Vector2 offset) {
		Float newX, newY;

		for (Vector2 point : points) {
			point.x += offset.x;
			point.y += offset.y;
		}

		if (componentList == null)
			return;

		for (JSONComponent component : componentList) {
			ComponentProperties originalProperties = component.getProperties();

			newX = Float.parseFloat(originalProperties.getPositionX())
					+ offset.x;
			newY = Float.parseFloat(originalProperties.getPositionY())
					+ offset.y;

			originalProperties.setPositionX(newX.toString());
			originalProperties.setPositionY(newY.toString());

			/*
			 * Set<Entry<String, String>> props = originalProperties.entrySet();
			 * 
			 * for (Entry<String, String> property : props) { if
			 * (property.getKey().compareTo(Properties.POSITION) == 0) {
			 * String[] values = property.getValue().split(","); newX =
			 * Float.parseFloat(values[0]) + offset.x; newY =
			 * Float.parseFloat(values[1]) + offset.y; } }
			 * 
			 * originalProperties.put(Properties.POSITION,
			 * Properties.makePostionString(newX, newY));
			 */

			component.setProperties(originalProperties);

		}

	}

	public ArrayList<Vector2> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Vector2> points) {
		this.points = points;
		this.smallPoints = null;
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
		Gson json = new Gson();
		// json.setIgnoreUnknownFields(true);
		return json.toJson(this);
	}

	public static JSONTrack objectify(String data) {
		Gson json = new Gson();
		// json.setIgnoreUnknownFields(true);
		return json.fromJson(data, JSONTrack.class);
	}

	@Override
	public boolean equals(Object obj) {
		
		JSONTrack otherTrack = ((JSONTrack) obj);
		
		return ((this.getItemIndex() == otherTrack.getItemIndex()) && this.getType() == otherTrack.getType());
		//return (this.getObjectId().compareTo(((JSONTrack) obj).getObjectId()) == 0);
		//return (this.jsonify().compareTo(((JSONTrack) obj).jsonify()) == 0);
	}

	public String getId() {
		String ret = "";
		Iterator<Vector2> pointIter = points.iterator();
		while (pointIter.hasNext()) {
			Vector2 joint = pointIter.next();
			ret += joint.toString();
		}

		ret += getType().name();

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

	public void setComponentJointTypes(
			HashMap<String, Integer> componentJointTypes) {
		this.componentJointTypes = componentJointTypes;
	}

}
