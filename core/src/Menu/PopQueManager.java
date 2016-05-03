package Menu;

import java.util.ArrayList;

import Dialog.BuyDialog;
import Dialog.DialogBase;
import Dialog.PauseDialog;
import Dialog.Skins;
import Dialog.SoundDialog;
import Dialog.StoreBuyDialog;
import Dialog.TextDialog;
import Dialog.WinDialog;
import Menu.PopQueObject.PopQueObjectType;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.gudesigns.climber.GameLoader;

public class PopQueManager {

	private ArrayList<PopQueObject> que = new ArrayList<PopQueObject>();
	private Stage stage;

	private DialogBase dialog;
	private PopQueObject currentMsg;

	private WinDialog winTable;
	private PauseDialog pauseTable;

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
		pauseTable = new PauseDialog(gameLoader, popQueObject);
	}

	public void update() {

		if (que.isEmpty()) {
			return;
		} else {
			currentMsg = pop();
			handlePop(currentMsg);
		}
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
		}
	}
	
	private void createPauseDialog(PopQueObject popQueObject) {
		pauseTable.update(gameLoader,popQueObject);
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

	private void createBuyDialog(PopQueObject popQueObject) {
		/*
		 * Skin skin = Skins.loadDefault();
		 * 
		 * dialog = new Text2ButtonDialog("Buy", skin, "default");
		 * dialog.setTouchable(Touchable.enabled); dialog.show(stage);
		 */

		dialog = BuyDialog.CreateDialog(gameLoader, popQueObject);
		dialog.show(stage).top();

	}

	private void createLostDialog(PopQueObject popQueObject) {
		dialog = new TextDialog("Killed", skin, "default");
		dialog.setTouchable(Touchable.disabled);
		dialog.show(stage);
		Animations.fadeIn(dialog);
	}

	private void createWinDialog(PopQueObject popQueObject) {
		/*
		 * dialog = new TextDialog("Win", skin, "default");
		 * dialog.setTouchable(Touchable.disabled); dialog.show(stage);
		 */

		// Table t = WinDialog.CreateDialog(popQueObject);
		winTable.update(gameLoader,popQueObject);
		stage.addActor(winTable.getBase());
	}

	private void createLoadingDialog(PopQueObject popQueObject) {
		dialog = new TextDialog("Loading", skin, "default");
		dialog.setTouchable(Touchable.disabled);
		dialog.setZIndex(1);
		dialog.show(stage);
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
