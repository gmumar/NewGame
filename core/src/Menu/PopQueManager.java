package Menu;

import java.util.ArrayList;

import wrapper.Globals;
import Dialog.CarDisplayDialog;
import Dialog.DialogBase;
import Dialog.KilledDialog;
import Dialog.PauseDialog;
import Dialog.Skins;
import Dialog.SoundDialog;
import Dialog.StoreBuyDialog;
import Dialog.TextDialog;
import Dialog.TutorialDialog;
import Dialog.UnlockDialog;
import Dialog.UpgradeDialog;
import Dialog.UserErrorDialog;
import Dialog.WinDialog;
import Menu.PopQueObject.PopQueObjectType;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.gudesigns.climber.GameLoader;

public class PopQueManager {

	private ArrayList<PopQueObject> que = new ArrayList<PopQueObject>();
	private Stage stage;

	private DialogBase dialog;
	private PopQueObject currentMsg;

	private WinDialog winTable;
	private PauseDialog pauseTable;
	private KilledDialog killedTable;
	private TutorialDialog tutotialTable;

	Skin skin;

	GameLoader gameLoader;

	public PopQueManager(GameLoader gameLoader, Stage stage) {
		this.stage = stage;
		this.gameLoader = gameLoader;
		skin = Skins.loadDefault(gameLoader, 0);
	}

	public void initWinTable(PopQueObject popQueObject) {
		winTable = new WinDialog(gameLoader, popQueObject);
	}

	public void initPauseTable(PopQueObject popQueObject) {
		pauseTable = new PauseDialog(gameLoader, this, popQueObject);
	}

	public void initKilledTable(PopQueObject popQueObject) {
		killedTable = new KilledDialog(gameLoader, popQueObject);
	}
	
	public void initTutorialTable(PopQueObject popQueObject) {
		tutotialTable = new TutorialDialog(gameLoader, popQueObject, stage);
	}

	public void update() {

		if (que.isEmpty()) {
			return;
		} else {
			currentMsg = pop();
			handlePop(currentMsg);
		}
		stage.act();
		stage.draw();
	}

	public void push(PopQueObject obj) {
		que.add(obj);
	}

	private PopQueObject pop() {
		PopQueObject obj = que.get(0);
		que.remove(0);
		return obj;
	}

	private void handlePop(PopQueObject popQueObject) {

		if (popQueObject.getType() == PopQueObjectType.TEST) {
			createDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.LOADING) {
			createLoadingDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.DELETE) {
			Animations.fadeAndHide(dialog);
		} else if (popQueObject.getType() == PopQueObjectType.BUY) {
			createBuyDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.WIN) {
			createWinDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.KILLED) {
			createLostDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.STORE_BUY) {
			createBuyStoreDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.SOUND) {
			createSoundDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.PAUSE) {
			createPauseDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.UNLOCK_MODE
				|| popQueObject.getType() == PopQueObjectType.UNLOCK_TRACK
				|| popQueObject.getType() == PopQueObjectType.UNLOCK_CAR_MODE
				|| popQueObject.getType() == PopQueObjectType.UNLOCK_ARCTIC_WORLD) {
			createUnlockModeDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.ERROR_PARTS_NOT_UNLOCKED
				|| popQueObject.getType() == PopQueObjectType.ERROR_USER_BUILD
				|| popQueObject.getType() == PopQueObjectType.ERROR_NOT_ENOUGH_MONEY) {
			createUserErrorDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.CAR_DISPLAY) {
			createCarDisplayDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.TUTORIAL_BUILDER_SCREEN
				|| popQueObject.getType() == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_INTRO
				|| popQueObject.getType() == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP1
				|| popQueObject.getType() == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP2
				|| popQueObject.getType() == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP3
				|| popQueObject.getType() == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP4
				|| popQueObject.getType() == PopQueObjectType.TUTORIAL_BUILDER_SCREEN_STEP5) {
			createTutorialDialog(popQueObject);
		} else {
			System.out.println("ERROR: Unknown PopQueObjectType: "
					+ popQueObject.getType().toString());
		}
	}

	private void createTutorialDialog(PopQueObject popQueObject) {
		//dialog = TutorialDialog.CreateDialog(gameLoader, stage, popQueObject);

		//dialog.show(stage);
		
		tutotialTable.setStep(popQueObject);
		stage.addActor(tutotialTable.getBase());
		//dialog.setPosition(0, 0);
	}

	private void createUserErrorDialog(PopQueObject popQueObject) {
		dialog = UserErrorDialog.CreateDialog(gameLoader, popQueObject);
		dialog.show(stage);

	}

	private void createUnlockModeDialog(PopQueObject popQueObject) {
		dialog = UnlockDialog.CreateDialog(gameLoader, popQueObject);
		if (popQueObject.getType() == PopQueObjectType.UNLOCK_TRACK) {
			dialog.show(stage).setWidth(200);
		} else {
			dialog.show(stage);
		}
	}

	private void createPauseDialog(PopQueObject popQueObject) {
		pauseTable.update(gameLoader, this, popQueObject);
		stage.addActor(pauseTable.getBase());
	}

	private void createSoundDialog(PopQueObject popQueObject) {
		stage.addActor(SoundDialog.CreateDialog(gameLoader, popQueObject));
	}

	private void createBuyStoreDialog(PopQueObject popQueObject) {
		/*
		 * Skin skin = Skins.loadDefault();
		 * 
		 * dialog = new Text2ButtonDialog("Buy", skin, "default");
		 * dialog.setTouchable(Touchable.enabled); dialog.show(stage);
		 */

		stage.addActor(StoreBuyDialog.CreateDialog(gameLoader, popQueObject));

	}

	private void createCarDisplayDialog(PopQueObject popQueObject) {

		dialog = CarDisplayDialog.CreateDialog(gameLoader, popQueObject);
		dialog.show(stage).top();

	}

	private void createBuyDialog(PopQueObject popQueObject) {
		/*
		 * Skin skin = Skins.loadDefault();
		 * 
		 * dialog = new Text2ButtonDialog("Buy", skin, "default");
		 * dialog.setTouchable(Touchable.enabled); dialog.show(stage);
		 */

		dialog = UpgradeDialog.CreateDialog(gameLoader, popQueObject);
		dialog.show(stage).top();

	}

	private void createLostDialog(PopQueObject popQueObject) {
		// dialog = new TextDialog("Killed", skin, "default");
		// dialog.setTouchable(Touchable.disabled);
		// dialog.show(stage);
		// Animations.fadeIn(dialog);
		killedTable.update(gameLoader, popQueObject);
		stage.addActor(killedTable.getBase());
	}

	private void createWinDialog(PopQueObject popQueObject) {
		/*
		 * dialog = new TextDialog("Win", skin, "default");
		 * dialog.setTouchable(Touchable.disabled); dialog.show(stage);
		 */

		// Table t = WinDialog.CreateDialog(popQueObject);
		winTable.updateMoney(gameLoader, popQueObject);
		stage.addActor(winTable.getBase());
	}

	private void createLoadingDialog(PopQueObject popQueObject) {
		//dialog = new TextDialog("Loading", skin, "default");
		dialog = new DialogBase("", skin, "default");
		dialog.background("transparent");
		
		dialog.row();
		
		Table content = new Table();
		
		//Label text = new Label("loading ", skin);
		//content.add(text);
		
		Image loading = new Image(gameLoader.Assets.getFilteredTexture("loading.png"));
		loading.setOrigin(Globals.baseSize, Globals.baseSize);
		content.add(loading).width(Globals.baseSize*2).height(Globals.baseSize*2).pad(4);
		
		Animations.rotate(loading);
		
		dialog.add(content).padBottom(10).padLeft(10).padRight(10).padTop(2);
		
		dialog.setTouchable(Touchable.disabled);
		dialog.setZIndex(1);
		dialog.show(stage).align(Align.top);
		// dialog.setPosition(300,450);
		dialog.setPosition((stage.getWidth() / 2) - Globals.baseSize*2, stage.getHeight()  - 120);
		Animations.fadeIn(dialog);
	}

	private void createDialog(PopQueObject popQueObject) {

		dialog = new TextDialog("blaa", skin, "default");
		dialog.setTouchable(Touchable.disabled);
		dialog.show(stage);
	}

	public void dispose() {
		winTable.clear();
		// skin.dispose();
	}

}
