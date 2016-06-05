package RESTWrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Backendless_JSONParser {
	
	public static Backendless_Car processDownloadedCars(String input){
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Backendless_Car.class , new Backendless_Deserializer());
		Gson json = builder.create();
		
		Backendless_Car obj = json.fromJson(input,Backendless_Car.class);
		
		return obj;
		
	}
	
	public static Backendless_Track processDownloadedTrack(String input){
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Backendless_Track.class , new Backendless_Deserializer());
		Gson json = builder.create();
		
		Backendless_Track obj = json.fromJson(input,Backendless_Track.class);
		
		return obj;
		
	}

}
