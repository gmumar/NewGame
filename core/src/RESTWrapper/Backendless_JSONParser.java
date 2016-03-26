package RESTWrapper;

import com.badlogic.gdx.utils.Json;

public class Backendless_JSONParser {
	
	public static Backendless_Car processDownloadedCars(String input){
		Json json = new Json();
		
		Backendless_Car obj = json.fromJson(Backendless_Car.class,input);
		
		//System.out.println(obj.getTotalObjects());
		
		return obj;
		
	}
	
	public static Backendless_Track processDownloadedTrack(String input){
		Json json = new Json();
		
		Backendless_Track obj = json.fromJson(Backendless_Track.class,input);
		
		//System.out.println(obj.getTotalObjects());
		
		return obj;
		
	}

}
