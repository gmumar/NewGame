package DataMutators;

import java.io.IOException;
import java.util.ArrayList;

import JSONifier.JSONTrack;

import com.badlogic.gdx.math.Vector2;

public class Compress {

	public static final String Track(String mapString) {

		JSONTrack jsonTrack = JSONTrack.objectify(mapString);
		ArrayList<Vector2> points = jsonTrack.getPoints();
		ArrayList<Float> smallPoints = new ArrayList<Float>();

		
		Vector2 prev = new Vector2(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY);
		
		for (Vector2 point : points) {
			
			if(prev.x != point.x){
				smallPoints.add(point.y);
				prev.x = point.x;
				prev.y = point.y;
			}
			
		}

		jsonTrack.setSmallPoints(smallPoints);
		

		String compressed = null;

		try {
			compressed = Gzip.compressToString(jsonTrack.jsonify());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return compressed;

	}

	public static final String Car(String carString) {

		String compressed = null;

		try {
			compressed = Gzip.compressToString(carString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return compressed;
	}

	public static String Challenge(String challenge) {

		String compressed = null;

		try {
			compressed = Gzip.compressToString(challenge);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return compressed;
	}

}
