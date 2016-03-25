package RESTWrapper;

import java.util.HashMap;

import wrapper.GamePreferences;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Preferences;

public class BackendFunctions {

	public void uploadCar(String inputString) {
		//Preferences prefs = Gdx.app
			//	.getPreferences(GamePreferences.CAR_PREF_STR);

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(RESTProperties.CAR_JSON,// "hello"
				//prefs.getString(GamePreferences.CAR_MAP_STR, "Error"));
				inputString);

		REST.postData(RESTPaths.CARS, parameters, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				//System.out.println(httpResponse.getResultAsString());

			}

			@Override
			public void failed(Throwable t) {
				// TODO Auto-generated method stub

			}

			@Override
			public void cancelled() {
				// TODO Auto-generated method stub

			}
		});

	}

	public void uploadTrack() {
		Preferences prefs = Gdx.app
				.getPreferences(GamePreferences.CAR_PREF_STR);

		String mapString = prefs.getString(GamePreferences.TRACK_MAP_STR, null);
		
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(RESTProperties.TRACK_POINTS_JSON,mapString);

		REST.postData(RESTPaths.MAPS, parameters, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				//System.out.println(httpResponse.getResultAsString());

			}

			@Override
			public void failed(Throwable t) {
				// TODO Auto-generated method stub

			}

			@Override
			public void cancelled() {
				// TODO Auto-generated method stub

			}
		});

	}

}
