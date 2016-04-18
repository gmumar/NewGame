package RESTWrapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

import DataMutators.Decompress;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Backendless_Deserializer implements JsonDeserializer<Backendless_Parent> {

	@Override
	public Backendless_Parent deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		
		Backendless_Parent ret = null;
	
		if(typeOfT.equals(Backendless_Car.class)){
			ret = new Backendless_Car();
		} else if (typeOfT.equals(Backendless_Track.class)){
			ret = new Backendless_Track();
		}
		
		JsonObject obj = json.getAsJsonObject();
		
		ret.setTotalObjects(obj.get("totalObjects").getAsInt());
		ret.setOffset(obj.get("offset").getAsInt());
		
		JsonArray localData = obj.get("data").getAsJsonArray();
		
		Iterator<JsonElement> iter = localData.iterator();
		ArrayList<String> tmp = new ArrayList<String>();
		while (iter.hasNext()) {
			JsonObject item = iter.next().getAsJsonObject();
			
			String actualDataStr = null;
			
			if(typeOfT.equals(Backendless_Car.class)){
				actualDataStr = Decompress.Car(item.get(RESTProperties.CAR_JSON).getAsString());
			} else if (typeOfT.equals(Backendless_Track.class)){
				actualDataStr = Decompress.Track(item.get(RESTProperties.TRACK_POINTS_JSON).getAsString());
			}
			//(RESTProperties.CAR_JSON, "UTF-8");
			
			tmp.add(actualDataStr);
		}
		
		ret.setData(tmp);
		return ret;
	}

}
