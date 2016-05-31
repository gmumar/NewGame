package RESTWrapper;

import java.util.HashMap;

import wrapper.Globals;
import DataMutators.Compress;

import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;

public class BackendFunctions {

	public static void uploadCar(String inputString, String fileName) {
		//Preferences prefs = Gdx.app
			//	.getPreferences(GamePreferences.CAR_PREF_STR);

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(RESTProperties.CAR_JSON,// "hello"
				//prefs.getString(GamePreferences.CAR_MAP_STR, "Error"));
				Compress.Car(inputString));

		REST.postData(fileName, parameters, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				//System.out.println(httpResponse.getResultAsString());
				Globals.toast("Car uploaded");
			}

			@Override
			public void failed(Throwable t) {
				Globals.toast("Car uploaded failed");
			}

			@Override
			public void cancelled() {
				Globals.toast("Car uploaded cancelled");

			}
		});

	}

	public static void uploadTrack(String mapString, String restPath, float mapTime, int difficulty, int index) {
		
		System.out.println("BackendFunctions: " + Integer.toString(difficulty));

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(RESTProperties.TRACK_POINTS_JSON, Compress.Track(mapString));
		parameters.put(RESTProperties.TRACK_BEST_TIME, Float.toString(mapTime));
		parameters.put(RESTProperties.TRACK_DIFFICULTY, Integer.toString(difficulty));
		parameters.put(RESTProperties.TRACK_INDEX, Integer.toString(index));

		REST.postData(restPath, parameters, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				Globals.toast("track uploaded");

			}

			@Override
			public void failed(Throwable t) {
				Globals.toast("track uploaded failed");
			}

			@Override
			public void cancelled() {
				Globals.toast("track uploaded cancelled");
			}
		});

	}

}
