package JSONifier;

import java.util.ArrayList;

public abstract class JSONParentClass {
	
	public enum JSONParentType {CAR, TRACK};

	private String objectId = null;

	public abstract ArrayList<JSONComponent> getComponentList() ;
	public abstract void setJointList(ArrayList<JSONJoint> joints) ;
	public abstract JSONParentType getParentType() ;
	public abstract ArrayList<JSONJoint> getJointList() ;
	public abstract String jsonify();
	

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

}
