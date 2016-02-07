package RESTWrapper;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;

public class Backendless_Object implements Json.Serializable {

	private int totalObjects;
	private ArrayList<String> data = new ArrayList<String>();

	@Override
	public void write(Json json) {

	}

	@Override
	public void read(Json json, JsonValue jsonData) {

		totalObjects = jsonData.getInt("totalObjects");

		JsonValue localData = jsonData.get("data");
		
		System.out.println(totalObjects);

		JsonIterator iter = localData.iterator();
		while (iter.hasNext()) {
			JsonValue item = iter.next();
			String b = item.getString(RESTProperties.CAR_JSON, "UTF-8");
			//byte[] bytes = Base64Coder.decode(b);//.decodeBase64(b);
			String car = null;

			System.out.println(b);
			
			try {
				car = Gzip.decompress(b);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println(car);
			data.add(car);
		}

	}

	public int getTotalObjects() {
		return totalObjects;
	}

	public ArrayList<String> getData() {
		return data;
	}

}
