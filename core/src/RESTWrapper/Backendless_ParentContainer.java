package RESTWrapper;

import java.util.ArrayList;

public class Backendless_ParentContainer {
	
	private int totalObjects;
	private int offset;
	private ArrayList<ServerDataUnit> data = new ArrayList<ServerDataUnit>();
	
	public int getTotalObjects() {
		return totalObjects;
	}
	public void setTotalObjects(int totalObjects) {
		this.totalObjects = totalObjects;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public ArrayList<ServerDataUnit> getData() {
		return data;
	}
	public void setData(ArrayList<ServerDataUnit> data) {
		this.data = data;
	}

	
}
