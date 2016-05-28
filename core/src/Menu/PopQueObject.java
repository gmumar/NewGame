package Menu;

import User.TwoButtonDialogFlow;

import com.gudesigns.climber.CarModeScreen;
import com.gudesigns.climber.GameModeScreen;
import com.gudesigns.climber.GamePlayScreen;

public class PopQueObject {

	public enum PopQueObjectType {
		TEST, DELETE, LOADING, BUY, WIN, KILLED, STORE_BUY, SOUND, PAUSE, UNLOCK_TRACK, UNLOCK_MODE, UNLOCK_CAR_MODE, USER_ERROR, USER_BUILD_ERROR
	};

	private PopQueObjectType type;
	private Integer nextLevel;
	private String componentName;
	private MenuBuilder menuBuilderInstance;
	private GamePlayScreen gamePlayScreenInstance;
	private GameModeScreen gameModeScreenInstance;
	private SelectorScreen selectorScreenInstance;
	private CarModeScreen carModeScreenInstance;
	private TwoButtonDialogFlow twoButtonFlowContext;
	// private MainMenuScreen mainMenuScreenInstance;

	private String unlockDialogHeader;
	private int unlockDialogPrice;
	private String unlockDialogDes;
	private String errorString;
	private String errorHeaderString;

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

	public PopQueObject(PopQueObjectType unlockMode, String text,
			String description, int price, TwoButtonDialogFlow context) {

		if (!(unlockMode == PopQueObjectType.UNLOCK_CAR_MODE
				|| unlockMode == PopQueObjectType.UNLOCK_MODE || unlockMode == PopQueObjectType.UNLOCK_TRACK))
			return;

		twoButtonFlowContext = context;
		type = unlockMode;
		unlockDialogHeader = text;
		unlockDialogPrice = price;
		unlockDialogDes = description;
	}

	public PopQueObject(PopQueObjectType userError, String header,
			String string, TwoButtonDialogFlow instance) {
		if (!(userError == PopQueObjectType.USER_ERROR || userError == PopQueObjectType.USER_BUILD_ERROR))
			return;

		twoButtonFlowContext = instance;
		type = userError;
		errorString = string;
		errorHeaderString = header;
	}

	public String getErrorHeaderString() {
		return errorHeaderString;
	}

	public String getErrorString() {
		return errorString;
	}

	public TwoButtonDialogFlow getTwoButtonFlowContext() {
		return twoButtonFlowContext;
	}

	public CarModeScreen getCarModeScreenInstance() {
		return carModeScreenInstance;
	}

	public SelectorScreen getSelectorScreenInstance() {
		return selectorScreenInstance;
	}

	public GameModeScreen getGameModeScreenInstance() {
		return gameModeScreenInstance;
	}

	public String getUnlockDialogDes() {
		return unlockDialogDes;
	}

	public void setUnlockDialogDes(String unlockDialogDes) {
		this.unlockDialogDes = unlockDialogDes;
	}

	public String getUnlockDialogHeader() {
		return unlockDialogHeader;
	}

	public void setUnlockDialogHeader(String unlockDialogHeader) {
		this.unlockDialogHeader = unlockDialogHeader;
	}

	public int getUnlockDialogPrice() {
		return unlockDialogPrice;
	}

	public void setUnlockDialogPrice(int unlockDialogPrice) {
		this.unlockDialogPrice = unlockDialogPrice;
	}

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
