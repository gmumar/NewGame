package JSONifier;

import java.util.ArrayList;

public abstract class JSONParentClass {
	
	public enum JSONParentType {CAR, TRACK};

	public abstract ArrayList<JSONComponent> getComponentList() ;
	public abstract void setJointList(ArrayList<JSONJoint> joints) ;
	public abstract JSONParentType getParentType() ;
	public abstract ArrayList<JSONJoint> getJointList() ;

}
