package Component;


public class ComponentProperties {
	
	private float restituition = -1;
	private float friction = -1;
	private float density = -1;
	private boolean setFixtureData = false;
	private String texture = null;
	
	public float getRestituition() {
		return restituition;
	}
	public void setRestituition(float restituition) {
		this.restituition = restituition;
	}
	public float getFriction() {
		return friction;
	}
	public void setFriction(float friction) {
		this.friction = friction;
	}
	public float getDensity() {
		return density;
	}
	public void setDensity(float density) {
		this.density = density;
	}
	public String getTexture() {
		return texture;
	}
	public void setTexture(String texture) {
		this.texture = texture;
	}
	public boolean isSetFixtureData() {
		return setFixtureData;
	}
	public void setSetFixtureData(boolean setFixtureData) {
		this.setFixtureData = setFixtureData;
	}
	

}
