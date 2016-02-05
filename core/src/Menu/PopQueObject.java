package Menu;

public class PopQueObject {
	
	public enum PopQueObjectType { TEST, DELETE, LOADING };

	private PopQueObjectType type;
	
	public PopQueObject(PopQueObjectType type){
		this.type = type;
	}
	
	public PopQueObjectType getType() {
		return type;
	}

	public void setType(PopQueObjectType type) {
		this.type = type;
	}
	
	
}
