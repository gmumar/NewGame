package Menu;

import com.gudesigns.climber.GamePlayScreen;

public class PopQueObject {

	public enum PopQueObjectType {
		TEST, DELETE, LOADING, BUY, WIN, KILLED, STORE_BUY, SOUND, PAUSE
	};

	private PopQueObjectType type;
	private Integer nextLevel;
	private String componentName;
	private MenuBuilder menuBuilderInstance;
	private GamePlayScreen gamePlayScreenInstance;
	//private MainMenuScreen mainMenuScreenInstance;

	public PopQueObject(PopQueObjectType type) {
		this.type = type;
	}

	public PopQueObject(PopQueObjectType type, String componentName,
			Integer nextLevel, MenuBuilder instance) {
		if (type != PopQueObjectType.BUY)
			return;
		this.type = type;
		this.nextLevel = nextLevel;
		this.componentName = componentName;
		this.menuBuilderInstance = instance;
	}

	public PopQueObject(PopQueObjectType type, GamePlayScreen instance) {
		if (!(type == PopQueObjectType.WIN || type == PopQueObjectType.PAUSE))
			return;
		this.type = type;
		this.gamePlayScreenInstance = instance;
	}

	/*public PopQueObject(PopQueObjectType type, MainMenuScreen instance) {
		if (type != PopQueObjectType.STORE_BUY)
			return;
		this.type = type;
		//this.mainMenuScreenInstance = instance;
	}*/

	//public MainMenuScreen getMainMenuInstance() {
	//	return mainMenuScreenInstance;
	//}

	public MenuBuilder getCallingInstance() {
		return menuBuilderInstance;
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

	public GamePlayScreen getGamePlayInstance() {
		return gamePlayScreenInstance;
	}

}
