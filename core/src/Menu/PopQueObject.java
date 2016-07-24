package Menu;

import UserPackage.TwoButtonDialogFlow;

import com.gudesigns.climber.CarModeScreen;
import com.gudesigns.climber.GameModeScreen;
import com.gudesigns.climber.GamePlayScreen;
import com.gudesigns.climber.SelectorScreens.SelectorScreen;

public class PopQueObject {

	public enum PopQueObjectType {
		TEST, DELETE, LOADING, BUY, WIN, KILLED, STORE_BUY, SOUND, PAUSE, UNLOCK_TRACK, UNLOCK_ARCTIC_WORLD, UNLOCK_MODE, UNLOCK_CAR_MODE, ERROR_PARTS_NOT_UNLOCKED, ERROR_USER_BUILD, CAR_DISPLAY, ERROR_NOT_ENOUGH_MONEY, TUTORIAL_BUILDER_SCREEN, TUTORIAL_BUILDER_SCREEN_STEP1, TUTORIAL_BUILDER_SCREEN_STEP2, TUTORIAL_BUILDER_SCREEN_STEP3, TUTORIAL_BUILDER_SCREEN_STEP4, TUTORIAL_BUILDER_SCREEN_INTRO, TUTORIAL_BUILDER_SCREEN_STEP5, CHALLENGE_FINALIZATION, USER_SIGN_IN, ERROR_USER_NAME_TAKEN
	};

	private PopQueObjectType type;
	private Integer nextLevel;
	private String itemToBeSoldName;
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
	private String carJson;
	private boolean Admin = false;

	public PopQueObject(PopQueObjectType type) {
		this.type = type;
	}

	public PopQueObject(PopQueObjectType type, String componentName,
			Integer nextLevel, MenuBuilder instance) {
		if (type != PopQueObjectType.BUY)
			return;
		this.type = type;
		this.nextLevel = nextLevel;
		this.itemToBeSoldName = componentName;
		this.menuBuilderInstance = instance;
	}

	public PopQueObject(PopQueObjectType type, GamePlayScreen instance) {
		if (!(type == PopQueObjectType.WIN || type == PopQueObjectType.PAUSE || type == PopQueObjectType.KILLED
				|| type == PopQueObjectType.CHALLENGE_FINALIZATION))
			return;
		this.type = type;
		this.gamePlayScreenInstance = instance;
	}

	public PopQueObject(PopQueObjectType type, TwoButtonDialogFlow instance) {
		if (!(type == PopQueObjectType.TUTORIAL_BUILDER_SCREEN
				|| type == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_INTRO
				|| type == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP1
				|| type == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP2
				|| type == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP3
				|| type == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP4 
				|| type == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP5
				|| type == PopQueObjectType.USER_SIGN_IN))
			return;
		this.type = type;
		this.twoButtonFlowContext = instance;
	}

	public PopQueObject(PopQueObjectType unlockMode, String itemToBeSold,
			String text, String description, int price,
			TwoButtonDialogFlow context) {

		if (!(unlockMode == PopQueObjectType.UNLOCK_CAR_MODE
				|| unlockMode == PopQueObjectType.UNLOCK_MODE
				|| unlockMode == PopQueObjectType.UNLOCK_TRACK || unlockMode == PopQueObjectType.UNLOCK_ARCTIC_WORLD))
			return;

		twoButtonFlowContext = context;
		type = unlockMode;
		unlockDialogHeader = text;
		unlockDialogPrice = price;
		unlockDialogDes = description;
		this.itemToBeSoldName = itemToBeSold;
	}

	public PopQueObject(PopQueObjectType userError, String header,
			String string, TwoButtonDialogFlow instance) {
		if (!(userError == PopQueObjectType.ERROR_PARTS_NOT_UNLOCKED
				|| userError == PopQueObjectType.ERROR_USER_BUILD || userError == PopQueObjectType.ERROR_NOT_ENOUGH_MONEY
				|| userError == PopQueObjectType.ERROR_USER_NAME_TAKEN))
			return;

		twoButtonFlowContext = instance;
		type = userError;
		errorString = string;
		errorHeaderString = header;
	}

	public PopQueObject(PopQueObjectType carDisplay, String carJson,
			boolean admin) {
		if (!(carDisplay == PopQueObjectType.CAR_DISPLAY))
			return;

		type = carDisplay;
		this.carJson = carJson;
		this.Admin = admin;

	}

	public String getCarJson() {
		return carJson;
	}

	public boolean isAdmin() {
		return Admin;
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

	public String getItemToBeBoughtName() {
		return itemToBeSoldName;
	}

	public void setComponentName(String componentName) {
		this.itemToBeSoldName = componentName;
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
