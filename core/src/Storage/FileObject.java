package Storage;

import java.util.ArrayList;
import java.util.Iterator;

import JSONifier.JSONCar;
import JSONifier.JSONTrack;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;

public class FileObject implements Serializable {
	
	private final static String CARS = "cars";
	private final static String TRACKS = "tracks";

	private ArrayList<JSONCar> cars = new ArrayList<JSONCar>();
	private ArrayList<JSONTrack> tracks = new ArrayList<JSONTrack>();
	
	public ArrayList<JSONCar> getCars() {
		return cars;
	}
	public void setCars(ArrayList<JSONCar> cars) {
		this.cars = cars;
	}
	public ArrayList<JSONTrack> getTracks() {
		return tracks;
	}
	public void setTracks(ArrayList<JSONTrack> tracks) {
		this.tracks = tracks;
	}
	
	@Override
	public void write(Json json) {
		
		if(cars !=null && !cars.isEmpty()){
			json.writeArrayStart(CARS);
			Iterator<JSONCar> carIter = cars.iterator();
			while(carIter.hasNext()){
				JSONCar car = carIter.next();
				json.writeValue(car.jsonify());
			}
			json.writeArrayEnd();
		}
		
		if(tracks !=null && !tracks.isEmpty()){
			json.writeArrayStart(TRACKS);
			Iterator<JSONTrack> trackIter = tracks.iterator();
			while(trackIter.hasNext()){
				JSONTrack track = trackIter.next();
				json.writeValue(track.jsonify());
			}
			json.writeArrayEnd();
		}
		
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		JsonValue carData = jsonData.get(CARS);
		JsonValue trackData = jsonData.get(TRACKS);
		
		if(carData!=null){
			JsonIterator carIter = carData.iterator();
			while (carIter.hasNext()) {
				JsonValue item = carIter.next();
				String b = item.asString();
	
				//System.out.println(b);
				
				/*try {
					car = Gzip.decompress(b);
				} catch (IOException e) {
					e.printStackTrace();
				}*/
	
				//System.out.println(car);
				cars.add(JSONCar.objectify(b));
			}
		}
		
		if(trackData!=null){
			JsonIterator trackIter = trackData.iterator();
			while (trackIter.hasNext()) {
				JsonValue item = trackIter.next();
				String b = item.asString();
	
				//System.out.println(b);
				
				/*try {
					car = Gzip.decompress(b);
				} catch (IOException e) {
					e.printStackTrace();
				}*/
	
				//System.out.println(car);
				tracks.add(JSONTrack.objectify(b));
			}
		}
		
	}
	
	public void append(FileObject objectIn) {
		
		//System.out.println(objectIn.getCars().size());
		
		Iterator<JSONCar> carIter = objectIn.getCars().iterator();
		while(carIter.hasNext()){
			JSONCar car = carIter.next();
			if(!this.cars.contains(car)){
				this.cars.add(car);
			}
		}
			
		Iterator<JSONTrack> trackIter = objectIn.getTracks().iterator();
		while(trackIter.hasNext()){
			JSONTrack track = trackIter.next();
			if(!this.tracks.contains(track)){
				this.tracks.add(track);
			}
		}
	}
	
}
