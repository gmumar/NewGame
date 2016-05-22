package Storage;

import java.util.ArrayList;
import java.util.Iterator;

import JSONifier.JSONCar;
import JSONifier.JSONTrack;

public class FileObject  {
	
	//private final static String CARS = "cars";
	//private final static String TRACKS = "tracks";

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
