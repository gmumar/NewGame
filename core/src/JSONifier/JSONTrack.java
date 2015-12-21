package JSONifier;

import java.io.Serializable;
import java.util.ArrayList;

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
		return json.toJson(this);
	}
	
	public static JSONTrack objectify(String str){
		Json json = new Json();
		return json.fromJson(JSONTrack.class, str);
	}

}
