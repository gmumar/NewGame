package Assembly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import Component.Component;
import GroundWorks.GroundUnitDescriptor;
import JSONifier.JSONComponentName;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AssembledTrack {

	private ArrayList<GroundUnitDescriptor> points;
	private Collection<Component> parts;
	private float totalTrackLength = -1;

	public AssembledTrack(ArrayList<GroundUnitDescriptor> points,
			Collection<Component> collection) {
		this.points = points;
		this.parts = collection;
		totalTrackLength = calculateTotalTrackLength();
	}

	private float calculateTotalTrackLength() {
		GroundUnitDescriptor lastPos = points.get(points.size() - 1);
		return lastPos.getEnd().dst(0, lastPos.getEnd().y);
	}
	
	public void destroyComponent(JSONComponentName name) {
		
		Iterator<Component> iter = parts.iterator();
		Component part;
		
		
		while(iter.hasNext()){
			part = iter.next();
			
			if(part.getjComponentName().getId().compareTo(name.getId())==0){
				part.destroyObject();
				iter.remove();
			}
		}
	}

	public float getTotalTrackLength() {
		return totalTrackLength;
	}

	public void setTotalTrackLength(float totalTrackLength) {
		this.totalTrackLength = totalTrackLength;
	}

	public ArrayList<GroundUnitDescriptor> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<GroundUnitDescriptor> points) {
		this.points = points;
	}

	public Collection<Component>  getParts() {
		return parts;
	}

	public void setParts(Collection<Component>  parts) {
		this.parts = parts;
	}
	
	public void draw(SpriteBatch batch){
		for(Component part : parts){
			part.draw(batch);
		}
	}


}
