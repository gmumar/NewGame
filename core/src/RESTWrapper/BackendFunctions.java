package RESTWrapper;

import java.util.HashMap;

import wrapper.Globals;
import DataMutators.Compress;

import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.gudesigns.climber.ChallengeLobbyScreen;
import com.gudesigns.climber.GamePlayScreen;

public class BackendFunctions {

	public static void getUserMoneyDelta(String objectId,
			HttpResponseListener listener) {

		// parameters.put(RESTProperties.OBJECT_ID,"571D352B-71BF-1C95-FF07-C2368B0B0100");

		REST.customFunctionCallGET("MoneyDelta", "objectId", objectId, listener);
	}

	public static void updateChallengeWinner(String objectID, String winner) {

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(RESTProperties.WINNER, winner);

		REST.updateEntry(RESTPaths.CHALLENGES, objectID, parameters,
				new HttpResponseListener() {

					@Override
					public void handleHttpResponse(HttpResponse httpResponse) {
						System.out.println(httpResponse.getResultAsString());
					}

					@Override
					public void failed(Throwable t) {
						System.out
								.println("BackendFunctions: Challenge update failed");
					}

					@Override
					public void cancelled() {

					}
				});

	}

	public static void registerUser(final ChallengeLobbyScreen context,
			final String userName) {
		// Preferences prefs = Gdx.app
		// .getPreferences(GamePreferences.CAR_PREF_STR);

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(RESTProperties.USER_NAME, userName);

		REST.postData(RESTPaths.GAME_USERS, parameters,
				new HttpResponseListener() {

					@Override
					public void handleHttpResponse(HttpResponse httpResponse) {
						context.userRegistrationComplete(userName, httpResponse);
					}

					@Override
					public void failed(Throwable t) {
						context.userRegistrationFailed(userName);
					}

					@Override
					public void cancelled() {

					}
				});

	}

	public static void uploadCar(String inputString, String fileName, int index) {
		// Preferences prefs = Gdx.app
		// .getPreferences(GamePreferences.CAR_PREF_STR);

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(RESTProperties.CAR_JSON,// "hello"
				// prefs.getString(GamePreferences.CAR_MAP_STR, "Error"));
				Compress.Car(inputString));
		parameters.put(RESTProperties.CAR_INDEX, Integer.toString(index));

		REST.postData(fileName, parameters, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
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

	public static void uploadTrack(String mapString, String restPath,
			float mapTime, int difficulty, int index) {

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(RESTProperties.TRACK_POINTS_JSON,
				Compress.Track(mapString));
		parameters.put(RESTProperties.TRACK_BEST_TIME, Float.toString(mapTime));
		parameters.put(RESTProperties.TRACK_DIFFICULTY,
				Integer.toString(difficulty));
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

	public static void uploadChallenge(final GamePlayScreen gamePlayScreen, String restPath, String challenge,
			String targetUser, String sourceUser, float mapTime, String reward) {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(RESTProperties.CHALLENGE, Compress.Challenge(challenge));
		parameters.put(RESTProperties.TARGET_USER, targetUser);
		parameters.put(RESTProperties.SOURCE_USER, sourceUser);
		parameters.put(RESTProperties.TRACK_BEST_TIME, Float.toString(mapTime));
		parameters.put(RESTProperties.CHALLENGE_REWARD, reward);
		
		REST.customFunctionCallPOST("submitChallenge", parameters, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				
				gamePlayScreen.challengeSubmitted(httpResponse.getResultAsString());
			}

			@Override
			public void failed(Throwable t) {
				t.printStackTrace();
			}

			@Override
			public void cancelled() {
				Globals.toast("Car uploaded cancelled");

			}
		});
		
		
		/*REST.postData(restPath, parameters, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				httpResponse.getResultAsString();
			}

			@Override
			public void failed(Throwable t) {
				t.printStackTrace();
			}

			@Override
			public void cancelled() {
				Globals.toast("Car uploaded cancelled");

			}
		});*/

	}

}
