package RESTWrapper;

import com.badlogic.gdx.utils.Json;

public class Backendless_JSONParser {
	
	public static void processDownloadedCars(String input){
		Json json = new Json();
		
		Backendless_Object obj = json.fromJson(Backendless_Object.class, input);
		
		System.out.println(obj.getTotalObjects());
		
		
	}

}
