package Menu;

public class PopQueObject {
	
	public enum PopQueObjectType { TEST, DELETE, LOADING, BUY, WIN, KILLED };

	private PopQueObjectType type;
	private Integer nextLevel;
	private String componentName;
	private MenuBuilder callingInstance;
	
	public PopQueObject(PopQueObjectType type){
		this.type = type;
	}
	
	public PopQueObject(PopQueObjectType type, String componentName, Integer nextLevel, MenuBuilder instance){
		if(type!=PopQueObjectType.BUY) return;
		this.type = type;
		this.nextLevel = nextLevel;
		this.componentName = componentName;
		this.callingInstance = instance;
	}

	public MenuBuilder getCallingInstance(){
		return callingInstance;
	}
	
	public Integer getNextLevel() {
		return nextLevel;
	}

	public void setNextLevel(Integer nextLevel) {
		this.nextLevel = nextLevel;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public PopQueObjectType getType() {
		return type;
	}

	public void setType(PopQueObjectType type) {
		this.type = type;
	}
	
	
}
