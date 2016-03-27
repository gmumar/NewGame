package Component;

import Component.Component.ComponentTypes;

public class ComponentProperties {

	private String r = "0";
	private String X = "0";
	private String Y = "0";
	private ComponentTypes t = ComponentTypes.PART;
	private boolean m = false;
	
	public String getRotation() {
		return r;
	}

	public String getPositionX() {
		return X;
	}

	public String getPositionY() {
		return Y;
	}
	
	public void setPositionX(String posX) {
		X = posX;
	}

	public void setPositionY(String posY) {
		Y = posY;
	}

	public boolean isMotor() {
		return m;
	}

	public void setProperties (String rotation, String positionX, String positionY, boolean isMotor, ComponentTypes type){
		this.r = rotation;
		this.X = positionX;
		this.Y = positionY;
		this.m = isMotor;
		this.t = type;
	}
	
}
