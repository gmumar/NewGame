package DataMutators;

import java.io.IOException;
import java.util.ArrayList;

import GroundWorks.GroundBuilder;
import JSONifier.JSONTrack;

import com.badlogic.gdx.math.Vector2;

public class Decompress {

	public static final String Track(String mapString) {

		JSONTrack jsonTrack = null;

		try {

			jsonTrack = JSONTrack.objectify(Gzip.decompress(mapString));
			ArrayList<Vector2> points = new ArrayList<Vector2>();
			ArrayList<Float> smallPoints = jsonTrack.getSmallPoints();

			float x = GroundBuilder.UNIT_LENGTH;


			for (Float point : smallPoints) {
				points.add(new Vector2(x, point));
				x += GroundBuilder.UNIT_LENGTH;
			}

			jsonTrack.setPoints(points);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonTrack.jsonify();
	}

	public static final String Car(String carString) {

		String decompressed = null;

		try {
			decompressed = Gzip.decompress(carString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return decompressed;
	}

}
