package JSONifier;

import java.util.ArrayList;

public abstract class JSONParentClass {
	
	public enum JSONParentType {CAR, TRACK};

	private String objectId = null;
	private String creationTime = null;
	private int itemIndex;

	public abstract ArrayList<JSONComponent> getComponentList() ;
	public abstract void setJointList(ArrayList<JSONJoint> joints) ;
	public abstract JSONParentType getParentType() ;
	public abstract ArrayList<JSONJoint> getJointList() ;
	public abstract String jsonify();
	public abstract boolean equals(Object obj);

	public String getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	

}
