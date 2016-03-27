package RESTWrapper;

import java.util.ArrayList;

public class Backendless_Parent {
	
	private int totalObjects;
	private int offset;
	private ArrayList<String> data = new ArrayList<String>();
	
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
	public ArrayList<String> getData() {
		return data;
	}
	public void setData(ArrayList<String> data) {
		this.data = data;
	}

	
}
