package JSONifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;

public class JSONTrack {

	// TODO: make it so its added to the json array
	enum TrackType implements Serializable {
		NORMAL;
	};

	private ArrayList<Vector2> points;
	private TrackType type = TrackType.NORMAL;
	

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
	
	public String jsonify(){
		Json json = new Json();
		json.setIgnoreUnknownFields(true);
		return json.toJson(this);
	}
	
	public static JSONTrack objectify(String str){
		Json json = new Json();
		json.setIgnoreUnknownFields(true);
		return json.fromJson(JSONTrack.class, str);
	}
	
	@Override
	public boolean equals(Object obj) {
		return (this.jsonify().compareTo(((JSONTrack)obj).jsonify())==0);
	}
	
	public String getId(){
		String ret = "";
		Iterator<Vector2> pointIter = points.iterator();
		while(pointIter.hasNext()){
			Vector2 joint = pointIter.next();
			ret += joint.toString();
		}
		
		if(type == TrackType.NORMAL){
			ret += TrackType.NORMAL.name();
		}
		
		return ret;
	}

}
