package RESTWrapper;

import java.lang.reflect.Type;
import java.util.ArrayList;

import DataMutators.Decompress;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Backendless_Deserializer implements
		JsonDeserializer<Backendless_ParentContainer> {

	@Override
	public Backendless_ParentContainer deserialize(JsonElement json,
			Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		// The dataSet contains the number of items, offset and each of the
		// items data
		Backendless_ParentContainer dataSet = null;

		if (typeOfT.equals(Backendless_Car.class)) {
			dataSet = new Backendless_Car();
		} else if (typeOfT.equals(Backendless_Track.class)) {
			dataSet = new Backendless_Track();
		}

		JsonObject obj = json.getAsJsonObject();
		
		dataSet.setTotalObjects(obj.get("totalObjects").getAsInt());
		dataSet.setOffset(obj.get("offset").getAsInt());

		JsonArray fromServerPack = obj.get("data").getAsJsonArray();

		// Iterator<JsonElement> iter = localData.iterator();
		ArrayList<ServerDataUnit> tmp = new ArrayList<ServerDataUnit>();
		// while (iter.hasNext()) {
		for (JsonElement iter : fromServerPack) {
			JsonObject fromServer = iter.getAsJsonObject();
			ServerDataUnit unit = new ServerDataUnit();
			String actualDataStr = null;
			
			unit.setCreationTime(fromServer.get(RESTProperties.CREATED)
					.getAsString());

			if (typeOfT.equals(Backendless_Car.class)) {
				actualDataStr = Decompress.Car(fromServer.get(
						RESTProperties.CAR_JSON).getAsString());
			} else if (typeOfT.equals(Backendless_Track.class)) {
				actualDataStr = Decompress.Track(fromServer.get(
						RESTProperties.TRACK_POINTS_JSON).getAsString());
			}
			unit.setObjectId(fromServer.get(RESTProperties.OBJECT_ID)
					.getAsString());
			unit.setData(actualDataStr);

			if (typeOfT.equals(Backendless_Car.class)) {
				;
			} else if (typeOfT.equals(Backendless_Track.class)) {
				unit.setTrackBestTime(fromServer.get(
						RESTProperties.TRACK_BEST_TIME).getAsFloat());
				unit.setTrackDifficulty(fromServer.get(
						RESTProperties.TRACK_DIFFICULTY).getAsInt());
				unit.setTrackIndex(fromServer.get(
						RESTProperties.TRACK_INDEX).getAsInt());
			}

			tmp.add(unit);
		}

		dataSet.setData(tmp);
		return dataSet;
	}

}
